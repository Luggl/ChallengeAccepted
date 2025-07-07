package de.thws.challengeaccepted.data.repository

import android.util.Log
import de.thws.challengeaccepted.data.dao.*
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.entities.Membership
import de.thws.challengeaccepted.network.GroupService
import java.text.SimpleDateFormat
import java.util.Locale

class GroupDashboardRepository(
    private val service: GroupService,
    private val gruppeDao: GruppeDao,
    private val challengeDao: ChallengeDao,
    private val membershipDao: MembershipDao
) {
    suspend fun refreshDashboardData(groupId: String) {
        try {
            val response = service.getGroupFeed(groupId)
            // KORRIGIERT: Zugriff über die neue verschachtelte Struktur
            val data = response.message.data

            // Challenge-Daten speichern (falls vorhanden)
            data.challenge?.let { challengeInfo ->
                val challengeEntity = Challenge(
                    challengeId = challengeInfo.challengeId,
                    gruppeId = groupId,
                    typ = challengeInfo.typ,
                    startdatum = parseTimestamp(challengeInfo.startdatum),
                    active = true // Annahme: Wenn eine Challenge da ist, ist sie aktiv
                )
                challengeDao.insertAll(listOf(challengeEntity))
            }

            // Mitglieder-Daten speichern
            val membershipEntities = data.members.map { memberInfo ->
                Membership(
                    userId = memberInfo.userId,
                    gruppeId = memberInfo.gruppeId,
                    isAdmin = false // API liefert diese Info nicht
                )
            }
            membershipDao.insertAll(membershipEntities)

        } catch (e: Exception) {
            Log.e("GroupDashboardRepo", "Fehler beim Parsen oder Speichern: ${e.message}", e)
        }
    }

    private fun parseTimestamp(dateString: String): Long {
        return try {
            // Format anpassen an "Tue, 08 Jul 2025 00:00:00 GMT"
            val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            format.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            Log.e("GroupDashboardRepo", "Datums-Parsing fehlgeschlagen für: $dateString")
            0L
        }
    }
}
