package de.thws.challengeaccepted.models


data class SurvivalChallengeRequest(
    val startdatum: String,  // im Format "yyyy-MM-dd"
    val sportarten: List<SurvivalSportart>
)

data class SurvivalSportartEntry(
    val sportart_id: String,
    val schwierigkeitsgrad: String
)
