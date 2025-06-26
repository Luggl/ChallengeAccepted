package de.thws.challengeaccepted.data.repository

import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.network.GroupService

class GroupRepository(private val api: GroupService) {

    suspend fun getGroupsForUser(userId: String, token: String): List<GroupResponse> {
        return api.getGroupsForUser(userId, "Bearer $token")
    }
}
