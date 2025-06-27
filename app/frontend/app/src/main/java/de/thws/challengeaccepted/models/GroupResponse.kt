package de.thws.challengeaccepted.models

data class GroupResponse(
    val gruppe_id: String,
    val gruppenname: String,
    val beschreibung: String?,
    val erstellt_am: String?,
    val gruppenbild: String?
)