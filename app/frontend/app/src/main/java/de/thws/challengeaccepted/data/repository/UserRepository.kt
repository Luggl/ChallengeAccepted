package de.thws.challengeaccepted.data.repository

import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.User

class UserRepository(private val userDao: UserDao) {

    suspend fun getUser(userId: String): User? {
        return userDao.getUser(userId)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}
