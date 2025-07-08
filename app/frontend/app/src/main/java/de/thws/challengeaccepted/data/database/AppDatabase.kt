package de.thws.challengeaccepted.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.thws.challengeaccepted.data.dao.*
import de.thws.challengeaccepted.data.entities.*

@Database(
    entities = [
        User::class,
        Gruppe::class,
        Membership::class,
        Challenge::class,
        Aufgabe::class,
        BeitragEntity::class // NEU: Die Tabelle für Feed-Beiträge
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // Definiere hier alle DAOs, damit Room sie kennt
    abstract fun userDao(): UserDao
    abstract fun gruppeDao(): GruppeDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun aufgabeDao(): AufgabeDao
    abstract fun membershipDao(): MembershipDao
    abstract fun beitragDao(): BeitragDao // NEU: Das DAO für Feed-Beiträge

    // Das Companion Object erstellt die Singleton-Instanz der Datenbank
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Gibt die bestehende Instanz zurück oder erstellt eine neue
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "challenge_accepted_database" // Name der Datenbank-Datei
                ).fallbackToDestructiveMigration() .build()
                INSTANCE = instance
                instance
            }
        }
    }
    suspend fun clearAllData() {
        beitragDao().clearAll()     // Beiträge referenzieren Aufgaben, Gruppen etc.
        aufgabeDao().clearAll()     // Aufgaben referenzieren Challenges
        challengeDao().clearAll()   // Challenges referenzieren Gruppen
        membershipDao().clearAll()  // Memberships referenzieren User + Gruppen
        gruppeDao().clearAll()      // Gruppen werden ganz am Ende gelöscht
        userDao().clearAll()        // User ggf. zuletzt (je nach App-Logik)
    }

}