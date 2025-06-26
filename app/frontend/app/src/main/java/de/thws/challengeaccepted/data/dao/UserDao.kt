package de.thws.challengeaccepted.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import de.thws.challengeaccepted.data.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): User?

    @Delete
    suspend fun deleteUser(user: User)
}
