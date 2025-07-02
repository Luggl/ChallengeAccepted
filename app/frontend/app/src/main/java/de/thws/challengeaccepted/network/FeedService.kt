package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface FeedService {
    @GET("feed")
    suspend fun getFeed(): FeedResponse
}