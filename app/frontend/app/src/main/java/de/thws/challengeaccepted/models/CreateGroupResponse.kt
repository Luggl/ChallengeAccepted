package de.thws.challengeaccepted.models

data class CreateGroupResponse(
    val message: String,
    val gruppe: GruppeDto
)

data class GruppeDto(
    val id: String,
    val name: String,
    val beschreibung: String?,
    val invite_link: String?
)