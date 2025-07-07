package de.thws.challengeaccepted.models

data class ChallengeCreateResponse(
    val success: Boolean,
    val data: ChallengeData?
)

data class ChallengeData(
    val challenge_id: String,
    val typ: String,
    val startdatum: String,
    val enddatum: String?
)