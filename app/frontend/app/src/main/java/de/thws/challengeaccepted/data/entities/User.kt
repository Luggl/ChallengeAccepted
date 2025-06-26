package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val username: String,
    val email: String,
    val streak: Int = 0, // Wenn im Backend nicht vorhanden, Standardwert!
    val profilbild: String? = null
)