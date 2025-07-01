package de.thws.challengeaccepted.models

data class UserApiModel(
    val id: String,
    val username: String,
    val email: String,
    val streak: Int,
    val profilbild: String?,
    val Kalender: Map<String, String>
)
