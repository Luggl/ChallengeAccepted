package de.thws.challengeaccepted.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thws.challengeaccepted.data.entities.BeitragEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BeitragDao {

    // Gibt alle Beiträge für eine Gruppe als Flow zurück
    @Query("SELECT * FROM beitraege WHERE gruppeId = :gruppeId ORDER BY beitragId DESC") // Annahme: Neueste zuerst
    fun getBeitraegeForGroupAsFlow(gruppeId: String): Flow<List<BeitragEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beitraege: List<BeitragEntity>)

    @Query("DELETE FROM beitraege WHERE gruppeId = :gruppeId")
    suspend fun clearBeitraegeForGroup(gruppeId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBeitraege(beitraege: List<BeitragEntity>)

}