package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey val challengeId: String,
    val gruppeId: String,
    val typ: String, // standard, survival etc.
    val startdatum: Long, // Timestamp
    val active: Boolean
)
