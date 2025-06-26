package de.thws.challengeaccepted

import android.app.Application
import androidx.room.Room
import de.thws.challengeaccepted.data.database.AppDatabase

class App : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "challenge-db"
        ).build()
    }
}
