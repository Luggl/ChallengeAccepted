package de.thws.challengeaccepted.data.dao

import androidx.room.*
import de.thws.challengeaccepted.data.entities.Aufgabe
import kotlinx.coroutines.flow.Flow

@Dao
interface AufgabeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(aufgaben: List<Aufgabe>)

    @Query("DELETE FROM aufgaben WHERE challengeId IN (:challengeIds)")
    suspend fun clearAufgabenForChallenges(challengeIds: List<String>)

    // Gibt alle Aufgaben für eine Challenge als Flow zurück
    @Query("SELECT * FROM aufgaben WHERE challengeId = :challengeId")
    fun getAufgabenForChallengeAsFlow(challengeId: String): Flow<List<Aufgabe>>

    @Query("DELETE FROM aufgaben")
    suspend fun clearAll()
}