package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beitraege")
data class BeitragEntity(
    @PrimaryKey val beitragId: String,
    val gruppeId: String, // Um Beiträge einer Gruppe zuzuordnen
    val beschreibung: String,
    val videoUrl: String?,
    val imageUrl: String? // Annahme, dass es auch ein Bild geben kann
)