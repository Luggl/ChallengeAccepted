package de.thws.challengeaccepted.data.dao

import androidx.room.*
import de.thws.challengeaccepted.data.entities.Challenge
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challenges: List<Challenge>)

    @Query("DELETE FROM challenges WHERE gruppeId = :gruppeId")
    suspend fun clearChallengesForGroup(gruppeId: String)

    // Gibt alle Challenges für eine Gruppe als Flow zurück
    @Query("SELECT * FROM challenges WHERE gruppeId = :gruppeId")
    fun getChallengesForGroupAsFlow(gruppeId: String): Flow<List<Challenge>>

    @Query("SELECT * FROM challenges WHERE gruppeId = :gruppeId AND active = 1 LIMIT 1")
    fun getActiveChallengeForGroupAsFlow(gruppeId: String): Flow<Challenge?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChallenges(challenges: List<Challenge>)

    @Query("DELETE FROM challenges")
    suspend fun clearAll()
}