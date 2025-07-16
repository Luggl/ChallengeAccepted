package de.thws.challengeaccepted.data.repository

import android.util.Log
import de.thws.challengeaccepted.data.dao.AufgabeDao
import de.thws.challengeaccepted.data.dao.ChallengeDao
import de.thws.challengeaccepted.data.entities.Aufgabe
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.models.*
import de.thws.challengeaccepted.network.ChallengeService
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(
    private val service: ChallengeService,
    private val challengeDao: ChallengeDao,
    private val aufgabeDao: AufgabeDao
) {

    // Holt den Flow der Challenges für eine Gruppe aus der lokalen DB
    fun getChallengesForGroup(groupId: String): Flow<List<Challenge>> {
        return challengeDao.getChallengesForGroupAsFlow(groupId)
    }

    // Holt den Flow der Aufgaben für eine Challenge aus der lokalen DB
    fun getAufgabenForChallenge(challengeId: String): Flow<List<Aufgabe>> {
        return aufgabeDao.getAufgabenForChallengeAsFlow(challengeId)
    }

    // Aktualisiert die Challenges und zugehörigen Aufgaben vom Server
    suspend fun refreshChallenges(groupId: String) {
        try {
            val response = service.getChallengesForGroup(groupId)
            val challengesFromApi = response.challenges

            if (challengesFromApi.isEmpty()) return

            val challengeEntities = mutableListOf<Challenge>()
            val aufgabeEntities = mutableListOf<Aufgabe>()
            val challengeIdsToDelete = challengesFromApi.map { it.challengeId }

            for (challengeApi in challengesFromApi) {
                challengeEntities.add(mapChallengeToEntity(challengeApi, groupId))
                for (aufgabeApi in challengeApi.aufgaben) {
                    aufgabeEntities.add(mapAufgabeToEntity(aufgabeApi, challengeApi.challengeId))
                }
            }

            // Transaktionale Operation: Alte Daten löschen, neue einfügen
            aufgabeDao.clearAufgabenForChallenges(challengeIdsToDelete)
            challengeDao.clearChallengesForGroup(groupId)
            challengeDao.insertAll(challengeEntities)
            aufgabeDao.insertAll(aufgabeEntities)

        } catch (e: Exception) {
            Log.e("ChallengeRepository", "Fehler beim Aktualisieren der Challenges: ${e.message}")
        }
    }

    // --- Mapping-Funktionen ---
    private fun mapChallengeToEntity(api: ChallengeApiResponse, groupId: String) = Challenge(
        challengeId = api.challengeId,
        gruppeId = groupId,
        typ = api.typ,
        startdatum = api.startdatum,
        active = api.active
    )

    private fun mapAufgabeToEntity(api: AufgabeApiResponse, challengeId: String) = Aufgabe(
        aufgabeId = api.aufgabeId,
        challengeId = challengeId,
        beschreibung = api.beschreibung,
        zielwert = api.zielwert,
        dauer = api.dauer,
        deadline = api.deadline,
        datum = api.datum,
        unit = api.unit,
        typ = api.typ,
        erfuellungId = api.erfuellungId
    )

    // --- Bestehende Funktionen zum Erstellen von Challenges ---
    suspend fun createStandardChallenge(groupId: String, request: StandardChallengeRequest): ChallengeCreateResponse {
        val response = service.createStandardChallenge(groupId, request)
        refreshChallenges(groupId) // Nach Erstellen die Liste aktualisieren
        return response
    }

    suspend fun createSurvivalChallenge(groupId: String, request: SurvivalChallengeRequest): ChallengeCreateResponse {
        val response = service.createSurvivalChallenge(groupId, request)
        refreshChallenges(groupId) // Nach Erstellen die Liste aktualisieren
        return response
    }
}