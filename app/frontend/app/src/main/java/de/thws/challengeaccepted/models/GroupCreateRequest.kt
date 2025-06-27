package de.thws.challengeaccepted.models

data class GroupCreateRequest(
    val name: String,
    val beschreibung: String?,
    val gruppenbild: String? = null // falls Base64 oder URL
)