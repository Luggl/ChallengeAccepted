package de.thws.challengeaccepted.data.repository

import android.content.Context
import android.util.Log
import de.thws.challengeaccepted.data.dao.BeitragDao
import de.thws.challengeaccepted.data.dao.ChallengeDao
import de.thws.challengeaccepted.data.dao.GruppeDao
import de.thws.challengeaccepted.data.dao.MembershipDao
import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.Aufgabe
import de.thws.challengeaccepted.data.entities.BeitragEntity
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.data.entities.Membership
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.models.VoteRequest
import de.thws.challengeaccepted.network.GroupService
import de.thws.challengeaccepted.network.TaskService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Locale

class GroupRepository(
    private val service: GroupService,
    private val context: Context,
    private val gruppeDao: GruppeDao,
    private val challengeDao: ChallengeDao,
    private val beitragDao: BeitragDao,
    private val membershipDao: MembershipDao,
    private val userDao: UserDao
) {

    fun getFeedForGroup(groupId: String): Flow<List<BeitragEntity>> =
        beitragDao.getBeitraegeForGroupAsFlow(groupId)

    fun getActiveChallenge(groupId: String): Flow<Challenge?> =
        challengeDao.getActiveChallengeForGroupAsFlow(groupId)

    fun getGroupDetails(groupId: String): Flow<Gruppe?> =
        gruppeDao.getGruppeByIdAsFlow(groupId)

    fun getMembersForGroup(groupId: String): Flow<List<User>> =
        membershipDao.getMembershipsForGroupAsFlow(groupId)
            .flatMapLatest { memberships ->
                val userIds = memberships.map { it.userId }
                if (userIds.isNotEmpty())
                    userDao.getUsersByIdsAsFlow(userIds)
                else
                    flowOf(emptyList())
            }

    private fun parseTimestamp(dateString: String): Long {
        return try {
            val format = java.text.SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz",
                java.util.Locale.ENGLISH
            )
            format.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun getAllGroups(): Flow<List<Gruppe>> = gruppeDao.getAlleGruppenAsFlow()

    suspend fun refreshGroupOverview() {
        try {
            val response = service.getGroupOverview()
            Log.d("GroupRepo", "API GroupOverview: ${response.message}")
            val gruppenEntities = response.message.map { groupResponse ->
                mapToEntity(groupResponse)
            }
            gruppeDao.insertAll(gruppenEntities)
            Log.d("GroupRepo", "Gruppen gespeichert: $gruppenEntities")
        } catch (e: Exception) {
            Log.e("GroupRepo", "Fehler beim Aktualisieren der Gruppen: ${e.message}")
        }
    }

    // Mapping Funktion
    private fun mapToEntity(response: GroupResponse): Gruppe {
        return Gruppe(
            gruppeId = response.gruppe_id,
            gruppenname = response.gruppenname,
            beschreibung = response.beschreibung,
            gruppenbild = response.gruppenbild,
            einladungscode = "",
            einladungscodeGueltigBis = 0L,
            aufgabe = response.aufgabe
        )
    }
    suspend fun voteBeitragGroup(beitragId: String, voteRequest: VoteRequest) {
        service.voteBeitragGroup(beitragId, voteRequest)
    }
    private val taskService = de.thws.challengeaccepted.network.ApiClient
        .getRetrofit(context)
        .create(TaskService::class.java)

    suspend fun getOpenTaskForGroup(groupId: String, userId: String): Aufgabe? {
        val taskApiResponse = taskService.getTasksForUser() // Holt alle Aufgaben des Users
        val aufgabeApi = taskApiResponse.message.Aufgaben
            .firstOrNull { it.gruppe_id == groupId && it.status == "offen" }
        return aufgabeApi?.let {
            Aufgabe(
                aufgabeId = it.aufgabe_id,
                challengeId = "",
                beschreibung = it.beschreibung,
                zielwert = it.zielwert,
                dauer = it.dauer?.toIntOrNull(),
                deadline = parseApiDateToMillis(it.deadline),
                datum = parseApiDateToMillis(it.datum),
                unit = it.unit,
                typ = ""
            )
        }
    }

    private fun parseApiDateToMillis(dateStr: String): Long? {
        return try {
            val format = java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.ENGLISH)
            format.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }


    suspend fun refreshGroupData(groupId: String) {
        try {
            val response = service.getGroupFeed(groupId)
            Log.d("GroupRepo", "API Response: $response")
            if (!response.message.success) {
                Log.e("GroupRepo", "API-Fehler: ${response.message.error}")
                return
            }
            val data = response.message.data
            Log.d("GroupRepo", "API RESPONSE data: $data")

            // Gruppe speichern
            val group = data.group
            val groupEntity = Gruppe(
                gruppeId = group.gruppeId,
                gruppenname = group.gruppenname,
                beschreibung = group.beschreibung,
                gruppenbild = group.gruppenbild,
                einladungscode = "",
                einladungscodeGueltigBis = 0L,
                aufgabe = group.aufgabe
            )
            Log.d("GroupRepo", "Speichere Gruppe: $groupEntity")
            gruppeDao.insertGruppe(groupEntity)

            // Challenge speichern
            data.challenge?.let { challengeInfo ->
                val challengeEntity = Challenge(
                    challengeId = challengeInfo.challengeId,
                    gruppeId = group.gruppeId,
                    typ = challengeInfo.typ,
                    startdatum = parseTimestamp(challengeInfo.startdatum),
                    active = true
                )
                Log.d("GroupRepo", "Speichere Challenge: $challengeEntity")
                challengeDao.insertAllChallenges(listOf(challengeEntity))
            }

            // User speichern
            val userEntities = data.members.map { memberInfo ->
                val user = User(
                    userId = memberInfo.userId,
                    username = memberInfo.username.ifEmpty { "Mitglied" },
                    email = "",
                    profilbild = memberInfo.profilbildUrl
                )
                Log.d("GroupRepo", "Speichere User: $user")
                user
            }
            userDao.insertUsers(*userEntities.toTypedArray())
            val fixedGroupId = group.gruppeId
            // Membership speichern
            val membershipEntities = data.members.map { memberInfo ->
                val membership = Membership(
                    userId = memberInfo.userId,
                    gruppeId = fixedGroupId,
                    isAdmin = memberInfo.isAdmin
                )
                Log.d("GroupRepo", "Speichere Membership: $membership")
                membership
            }
            membershipDao.insertAllMemberships(membershipEntities)

            // Feed speichern
            val feedEntities = data.feed.map { feedItem ->
                val beitrag = BeitragEntity(
                    beitrag_Id = feedItem.beitrag_id,
                    gruppe_id = group.gruppeId,
                    beschreibung = feedItem.beschreibung,
                    video_url = feedItem.video_url,
                    erstellt_am    = feedItem.erstellt_am,
                    thumbnail_url = feedItem.thumbnail_url,
                    user_id = feedItem.user_id,
                )
                Log.d("GroupRepo", "Speichere Feed/Beitrag: $beitrag")
                beitrag
            }
            beitragDao.clearBeitraegeForGroup(group.gruppeId)
            beitragDao.insertAllBeitraege(feedEntities)

            // Debug: Hole alle Gruppen aus DB nach dem Insert (nur zum Debug, kann danach raus)
            try {
                val gruppenInDb = gruppeDao.getAlleGruppenAsFlow() // suspend Funktion!
                Log.d("GroupRepo", "Alle Gruppen in DB nach Update: $gruppenInDb")
            } catch (e: Exception) {
                Log.e("GroupRepo", "Fehler beim Lesen aus der DB: ${e.message}")
            }

        } catch (e: Exception) {
            Log.e("GroupRepo", "Fehler beim Aktualisieren der Gruppendaten: ${e.message}", e)
        }
    }
}
