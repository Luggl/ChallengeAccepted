package de.thws.challengeaccepted.models

// This is the one and only definition of this class.
data class GroupFeedItem(
    val aufgabe_anzahl: Int,
    val aufgabe_sportart: String,
    val aufgabe_unit: String,
    val beitrag_id: String,
    val beschreibung: String,
    val erstellt_am: String?,
    val gruppe_id: String,
    val thumbnail_url: String?,
    val user_id: String,
    val user_vote: String?,
    val video_url: String?
)

