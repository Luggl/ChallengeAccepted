package de.thws.challengeaccepted.models

import com.google.gson.annotations.SerializedName

// Die oberste Ebene der JSON-Antwort
data class GroupDashboardApiResponse(
    val message: GroupDashboardMessage
)

// Das "message"-Objekt, das die eigentlichen Daten enthält
data class GroupDashboardMessage(
    val data: GroupDashboardData,
    val error: String?,
    val success: Boolean
)

// Das "data"-Objekt mit allen Informationen
data class GroupDashboardData(
    val challenge: ChallengeInfo?, // Kann null sein
    val feed: List<GroupFeedItem>,
    val group: GroupInfo,
    val members: List<MemberInfo>
)

// --- Die restlichen Modelle bleiben wie gehabt ---

data class ChallengeInfo(
    @SerializedName("challenge_id")
    val challengeId: String,
    @SerializedName("ersteller_user_id")
    val erstellUserId: String,
    val startdatum: String,
    val typ: String
)

data class GroupInfo(
    @SerializedName("gruppenbild_url")
    val gruppenbildUrl: String?,
    val gruppenname: String
)

data class MemberInfo(
    @SerializedName("gruppe_id")
    val gruppeId: String,
    val labels: String?,
    @SerializedName("user_id")
    val userId: String
)
