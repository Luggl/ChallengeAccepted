package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gruppen")
data class Gruppe(
    @PrimaryKey val gruppeId: String, // UUID als String
    val gruppenname: String,
    val beschreibung: String?,
    val gruppenbild: String?,
    val einladungscode: String,
    val einladungscodeGueltigBis: Long // Timestamp in Millisekunden
)
