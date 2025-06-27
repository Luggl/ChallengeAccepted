package de.thws.challengeaccepted.data.repository

import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.network.GroupService

class GroupRepository(private val groupService: GroupService) {
    suspend fun getGroupOverview(): List<GroupResponse> {
        return groupService.getGroupOverview().message.data
    }
}