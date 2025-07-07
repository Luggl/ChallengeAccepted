package de.thws.challengeaccepted.data.dao

import androidx.room.*
import de.thws.challengeaccepted.data.entities.Gruppe
import kotlinx.coroutines.flow.Flow

@Dao
interface GruppeDao {

    // NEU: Gibt alle Gruppen als Flow zurück.
    @Query("SELECT * FROM gruppen")
    fun getAlleGruppenAsFlow(): Flow<List<Gruppe>>

    // NEU: Fügt eine ganze Liste von Gruppen auf einmal ein.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gruppen: List<Gruppe>)

    // NEU: Leert die Tabelle.
    @Query("DELETE FROM gruppen")
    suspend fun clearAll()

    // NEU: Kombiniert das Leeren und Einfügen in einer sicheren Transaktion.
    @Transaction
    suspend fun syncGruppen(gruppen: List<Gruppe>) {
        clearAll()
        insertAll(gruppen)
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGruppe(gruppe: Gruppe)

}