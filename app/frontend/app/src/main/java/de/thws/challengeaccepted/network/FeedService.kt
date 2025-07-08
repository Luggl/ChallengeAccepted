package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.FeedResponse
import de.thws.challengeaccepted.models.GroupFeedResponse
import de.thws.challengeaccepted.models.VoteRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FeedService {
    @GET("feed")
    suspend fun getFeed(): FeedResponse

    @GET("groupfeed")
    suspend fun getGroupFeed(
        @Query("group_id") groupId: String,
        @Header("Authorization") token: String
    ): GroupFeedResponse

    @POST("vote")
    suspend fun voteBeitrag(
        @Query("beitrag_id") beitragId: String,
        @Body voteRequest: VoteRequest
    )
}