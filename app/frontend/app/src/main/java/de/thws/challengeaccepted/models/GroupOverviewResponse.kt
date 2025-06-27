package de.thws.challengeaccepted.models

data class GroupOverviewResponse(
    val message: MessageData
)

data class MessageData(
    val data: List<GroupResponse>,
    val error: String?,
    val success: Boolean
)