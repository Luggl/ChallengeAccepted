package de.thws.challengeaccepted.data.repository

import android.util.Log
import de.thws.challengeaccepted.data.dao.GruppeDao
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.network.GroupService
import kotlinx.coroutines.flow.Flow

class GroupRepository(
    private val service: GroupService,
    private val dao: GruppeDao
) {

    // Die UI holt sich die Gruppen über diesen Flow direkt aus der Datenbank.
    val gruppen: Flow<List<Gruppe>> = dao.getAlleGruppenAsFlow()

    // Diese Funktion aktualisiert die Daten aus dem Netzwerk.
    suspend fun refreshGruppen() {
        try {
            val response = service.getGroupOverview()
            val gruppenEntities = response.message.map { groupResponse ->
                mapToEntity(groupResponse)
            }
            dao.syncGruppen(gruppenEntities)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Fehler beim Aktualisieren der Gruppen: ${e.message}")
        }
    }

    private fun mapToEntity(response: GroupResponse): Gruppe {
        return Gruppe(
            gruppeId = response.gruppe_id,
            gruppenname = response.gruppenname,
            beschreibung = response.beschreibung,
            gruppenbild = response.gruppenbild,
            einladungscode = "", // Standardwert, da nicht von diesem Endpunkt geliefert
            einladungscodeGueltigBis = 0L // Standardwert
        )
    }
}