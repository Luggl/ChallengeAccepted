package de.thws.challengeaccepted.models

import com.google.gson.annotations.SerializedName

// Die komplette Antwort vom Server für eine Liste von Challenges
data class ChallengeListResponse(
    val challenges: List<ChallengeApiResponse>
)

// Repräsentiert eine einzelne Challenge vom Server, inklusive ihrer Aufgaben
data class ChallengeApiResponse(
    @SerializedName("challenge_id")
    val challengeId: String,
    val typ: String,
    val startdatum: Long,
    val active: Boolean,
    val aufgaben: List<AufgabeApiResponse> // Jede Challenge enthält ihre Aufgaben
)

// Repräsentiert eine einzelne Aufgabe vom Server
data class AufgabeApiResponse(
    @SerializedName("aufgabe_id")
    val aufgabeId: String,
    val erfuellungId: String,
    val beschreibung: String,
    val zielwert: Int,
    val dauer: Int?,
    val deadline: Long?,
    val datum: Long?,
    val unit: String,
    val typ: String
)