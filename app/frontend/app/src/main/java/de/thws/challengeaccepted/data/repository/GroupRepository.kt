package de.thws.challengeaccepted.data.repository

import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.network.GroupService

class GroupRepository(private val service: GroupService) {

    suspend fun getGroupOverview(): List<GroupResponse> {
        val response = service.getGroupOverview()
        return response.message
    }
}
