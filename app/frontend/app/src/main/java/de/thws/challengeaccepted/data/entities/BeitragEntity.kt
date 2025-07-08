package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beitraege")
data class BeitragEntity(
    @PrimaryKey val beitrag_Id: String,
    val beschreibung: String,
    val erstellt_am: String?,
    val gruppe_id: String,
    val user_id: String,
    val video_url: String?,
    val thumbnail_url: String?,
    var user_vote: String? = null
)