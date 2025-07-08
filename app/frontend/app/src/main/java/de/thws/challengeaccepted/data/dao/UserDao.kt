package de.thws.challengeaccepted.data.dao

import androidx.room.*
import de.thws.challengeaccepted.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserAsFlow(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(vararg users: User)

    @Update
    suspend fun updateUser(user: User)

    // NEU: User direkt über die ID löschen
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    fun getUsersByIdsAsFlow(userIds: List<String>): Flow<List<User>>

    @Query("DELETE FROM users")
    suspend fun clearAll()
}