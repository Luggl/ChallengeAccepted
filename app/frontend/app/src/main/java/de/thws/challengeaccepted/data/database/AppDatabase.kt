package de.thws.challengeaccepted.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.*

@Database(
    entities = [User::class, Gruppe::class, Membership::class, Challenge::class, Aufgabe::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
