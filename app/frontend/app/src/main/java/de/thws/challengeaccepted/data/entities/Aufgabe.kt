package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aufgaben")
data class Aufgabe(
    @PrimaryKey val aufgabeId: String,
    val challengeId: String,
    val beschreibung: String,
    val zielwert: Int,
    val dauer: Int?,
    val deadline: Long?,
    val datum: Long?,
    val unit: String,
    val typ: String
)
