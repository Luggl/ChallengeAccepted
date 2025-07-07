package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.GroupFeedApiResponse

import retrofit2.http.GET
import retrofit2.http.Query

interface GroupFeedService {
    @GET("groupfeed")
    suspend fun getGroupFeed(@Query("group_id") groupId: String): GroupFeedApiResponse
}
