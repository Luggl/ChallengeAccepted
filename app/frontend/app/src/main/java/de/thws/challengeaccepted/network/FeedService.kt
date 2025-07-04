package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.FeedResponse
import de.thws.challengeaccepted.models.GroupFeedResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FeedService {
    @GET("feed")
    suspend fun getFeed(): FeedResponse

    @GET("groupfeed")
    suspend fun getGroupFeed(
        @Query("group_id") groupId: String,
        @Header("Authorization") token: String
    ): GroupFeedResponse
}