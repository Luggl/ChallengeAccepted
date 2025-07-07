package de.thws.challengeaccepted.models

/**
 * Die äußere Klasse, die die gesamte JSON-Antwort vom Server abbildet.
 */
data class RegistrationResponse(
    val message: String,
    val user: UserData // Enthält das verschachtelte User-Objekt
)

/**
 * Die innere Klasse, die nur die reinen Benutzerdaten enthält.
 */
data class UserData(
    val id: String,
    val username: String,
    val email: String
)