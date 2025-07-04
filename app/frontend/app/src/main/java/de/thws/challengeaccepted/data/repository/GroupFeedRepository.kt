package de.thws.challengeaccepted.data.repository

import android.content.Context
import de.thws.challengeaccepted.models.GroupFeedItem
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.GroupFeedService

class GroupFeedRepository(private val context: Context) {
    private val service = ApiClient.getRetrofit(context).create(GroupFeedService::class.java)

    suspend fun getGroupFeed(groupId: String): List<GroupFeedItem> {
        val response = service.getGroupFeed(groupId)
        return response.message
    }
}
