package de.thws.challengeaccepted.data.repository

import android.util.Log
import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.models.toRoomUser
import de.thws.challengeaccepted.network.UserService
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val service: UserService,
    private val dao: UserDao
) {

    fun getUserFlow(userId: String): Flow<User?> = dao.getUserAsFlow(userId)

    // GEÄNDERT: Die Funktion gibt jetzt die Kalenderdaten zurück
    suspend fun refreshUser(): Map<String, String> {
        return try {
            // 1. Daten vom Server holen
            val response = service.getUser()
            val userFromApi = response.user

            // 2. User in die Datenbank speichern
            val userEntity = userFromApi.toRoomUser()
            dao.insertUsers(userEntity)

            // 3. Kalenderdaten zurückgeben
            userFromApi.Kalender

        } catch (e: Exception) {
            Log.e("UserRepository", "Fehler beim Aktualisieren des Users: ${e.message}")
            // Bei Fehler eine leere Map zurückgeben
            emptyMap()
        }
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }

    suspend fun deleteCurrentUser(userId: String) {
        service.deleteUser(userId)
        dao.deleteUserById(userId)
    }
}