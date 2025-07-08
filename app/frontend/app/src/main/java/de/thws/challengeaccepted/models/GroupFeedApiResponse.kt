package de.thws.challengeaccepted.models

import com.google.gson.annotations.SerializedName

// Die oberste Ebene der JSON-Antwort von /api/groupfeed
data class GroupFeedApiResponse(
    val message: GroupFeedMessage
)

// Das "message"-Objekt, das die eigentlichen Daten enthält
data class GroupFeedMessage(
    val data: GroupFeedData,
    val error: String?,
    val success: Boolean
)

// Das "data"-Objekt mit allen Informationen
data class GroupFeedData(
    val challenge: ApiChallengeInfo?, // Kann null sein
    val feed: List<ApiFeedItem>,
    val group: ApiGroupInfo,
    val members: List<ApiMemberInfo>
)

// --- Unter-Modelle für die API-Antwort ---

data class ApiChallengeInfo(
    @SerializedName("challenge_id")
    val challengeId: String,
    @SerializedName("ersteller_user_id")
    val erstellUserId: String,
    val startdatum: String,
    val typ: String
)

data class ApiGroupInfo(
    val beschreibung: String?,
    @SerializedName("gruppe_id")
    val gruppeId: String,
    @SerializedName("gruppenbild")
    val gruppenbild: String?,
    val gruppenname: String,
    val aufgabe: Boolean
)

data class ApiMemberInfo(
    @SerializedName("gruppe_id")
    val gruppeId: String,
    val isAdmin: Boolean,
    @SerializedName("profilbild_url")
    val profilbildUrl: String?,
    @SerializedName("user_id")
    val userId: String,
    val username: String
)
data class ApiFeedItem(
    val beitrag_id: String,
    val beschreibung: String,
    val video_url: String?,
    val thumbnail_url: String?
)
