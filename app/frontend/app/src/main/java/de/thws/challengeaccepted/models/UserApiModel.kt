package de.thws.challengeaccepted.models

import com.google.gson.annotations.SerializedName

// Repräsentiert einen User, wie er von der API gesendet wird
data class UserApiModel(
    val id: String,
    val username: String,
    val email: String,
    val streak: Int,

    @SerializedName("profilbild_url") // Mappt JSON-Feld auf das Kotlin-Feld
    val profilbildUrl: String?,

    @SerializedName("Kalender") // Großbuchstabe wie im Backend
    val Kalender: Map<String, String>
)