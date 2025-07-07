package de.thws.challengeaccepted.data.repository

import android.util.Log
import de.thws.challengeaccepted.data.dao.*
import de.thws.challengeaccepted.data.entities.*
import de.thws.challengeaccepted.models.*
import de.thws.challengeaccepted.network.GroupFeedService
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale

class GroupFeedRepository(
    private val service: GroupFeedService,
    private val gruppeDao: GruppeDao,
    private val challengeDao: ChallengeDao,
    private val membershipDao: MembershipDao,
    private val beitragDao: BeitragDao
) {

    fun getFeedForGroup(groupId: String): Flow<List<BeitragEntity>> =
        beitragDao.getBeitraegeForGroupAsFlow(groupId)

    fun getActiveChallenge(groupId: String): Flow<Challenge?> =
        challengeDao.getActiveChallengeForGroupAsFlow(groupId)

    suspend fun refreshAllDashboardData(groupId: String) {
        try {
            val response = service.getGroupFeed(groupId)
            if (!response.message.success) {
                Log.e("GroupFeedRepo", "API-Fehler: ${response.message.error}")
                return
            }

            val data = response.message.data

            // 1. Gruppendaten speichern
            val group = data.group // Typ: ApiGroupInfo
            val groupEntity = Gruppe(
                gruppeId = group.gruppeId,
                gruppenname = group.gruppenname,
                beschreibung = group.beschreibung,
                gruppenbild = group.gruppenbild,
                einladungscode = "",
                einladungscodeGueltigBis = 0L
            )
            gruppeDao.insertGruppe(groupEntity)

            // 2. Challenge-Daten speichern
            data.challenge?.let { challengeInfo ->
                val challengeEntity = Challenge(
                    challengeId = challengeInfo.challengeId,
                    gruppeId = group.gruppeId, // von group, da API so liefert!
                    typ = challengeInfo.typ,
                    startdatum = parseTimestamp(challengeInfo.startdatum),
                    active = true
                )
                challengeDao.insertAllChallenges(listOf(challengeEntity))
            }

            // 3. Mitglieder-Daten speichern
            val membershipEntities = data.members.map { memberInfo ->
                Membership(
                    userId = memberInfo.userId,
                    gruppeId = memberInfo.gruppeId,
                    isAdmin = memberInfo.isAdmin
                )
            }
            membershipDao.insertAllMemberships(membershipEntities)

            // 4. Feed-Daten speichern
            val feedEntities = data.feed.map { feedItem ->
                BeitragEntity(
                    beitragId = feedItem.beitrag_id,
                    gruppeId = group.gruppeId, // von group
                    beschreibung = feedItem.beschreibung,
                    videoUrl = feedItem.video_url,
                    imageUrl = feedItem.thumbnail_url
                )
            }
            beitragDao.clearBeitraegeForGroup(group.gruppeId)
            beitragDao.insertAllBeitraege(feedEntities)

        } catch (e: Exception) {
            Log.e("GroupFeedRepo", "Fehler beim Aktualisieren der Dashboard-Daten: ${e.message}", e)
        }
    }

    private fun parseTimestamp(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            format.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
