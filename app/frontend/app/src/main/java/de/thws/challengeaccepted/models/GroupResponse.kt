package de.thws.challengeaccepted.models

data class GroupResponse(
    val gruppe_id: String,
    val gruppenname: String,
    val beschreibung: String?,
    val gruppenbild: String?,
    val einladungscode: String,
    val einladungscode_gueltig_bis: Long
)
