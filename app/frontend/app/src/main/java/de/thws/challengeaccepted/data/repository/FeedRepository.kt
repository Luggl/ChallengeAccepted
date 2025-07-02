package de.thws.challengeaccepted.data.repository

import android.content.Context
import de.thws.challengeaccepted.models.FeedResponse // <-- Wichtig!
import de.thws.challengeaccepted.models.Beitrag
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.FeedService

class FeedRepository(private val context: Context) {
    private val feedService = ApiClient.getRetrofit(context).create(FeedService::class.java)

    suspend fun getFeedItems(): List<Beitrag> {
        val response: FeedResponse = feedService.getFeed()
        return response.feed.data
    }
}
