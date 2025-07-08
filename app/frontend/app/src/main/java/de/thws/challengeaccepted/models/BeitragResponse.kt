package de.thws.challengeaccepted.models

data class FeedResponse(
    val feed: FeedData
)

data class FeedData(
    val data: List<Beitrag>
)

data class Beitrag(
    val beitrag_id: String,
    val beschreibung: String,
    val erstellt_am: String?,
    val gruppe_id: String,
    val user_id: String,
    val video_url: String?,
    val thumbnail_url: String?,
    var user_vote: String? = null
)
