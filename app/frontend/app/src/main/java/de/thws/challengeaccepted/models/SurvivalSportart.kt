package de.thws.challengeaccepted.models
import java.io.Serializable

data class SurvivalSportart(
    val sportart_id: String,
    val schwierigkeitsgrad: String  // z.B. "easy", "medium", "hard"
) : Serializable