package de.thws.challengeaccepted.models

data class PasswordResetConfirmRequest(
    val token: String,
    val newPassword: String
)