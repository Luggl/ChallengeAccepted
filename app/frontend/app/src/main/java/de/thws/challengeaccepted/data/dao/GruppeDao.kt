package de.thws.challengeaccepted.data.dao

import androidx.room.*
import de.thws.challengeaccepted.data.entities.Gruppe
import kotlinx.coroutines.flow.Flow

@Dao
interface GruppeDao {

    // Gibt alle Gruppen als Flow zurück.
    @Query("SELECT * FROM gruppen")
    fun getAlleGruppenAsFlow(): Flow<List<Gruppe>>

    // NEU: Diese Funktion hat gefehlt. Sie holt eine einzelne Gruppe als Flow.
    @Query("SELECT * FROM gruppen WHERE gruppeId = :id")
    fun getGruppeByIdAsFlow(id: String): Flow<Gruppe?>

    // Fügt eine ganze Liste von Gruppen auf einmal ein.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gruppen: List<Gruppe>)

    // Leert die Tabelle.
    @Query("DELETE FROM gruppen")
    suspend fun clearAll()

    // Kombiniert das Leeren und Einfügen in einer sicheren Transaktion.
    @Transaction
    suspend fun syncGruppen(gruppen: List<Gruppe>) {
        clearAll()
        insertAll(gruppen)
    }

    // Fügt eine einzelne Gruppe ein (weiterhin nützlich)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGruppe(gruppe: Gruppe)

}