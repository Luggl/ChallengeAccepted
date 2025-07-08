package de.thws.challengeaccepted.models

// Ein einzelner Eintrag für Sportart + Start-/Zielintensität
data class SportartIntensity(
    val sportart_id: String,
    val startintensität: String,
    val zielintensität: String
)

// Der komplette Request für die Challenge-Erstellung
data class StandardChallengeRequest(
    val startdatum: String,       // Format: "2025-07-12"
    val enddatum: String,         // Format: "2025-07-19"
    val sportarten: List<SportartIntensity>
)