package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.GroupResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header


interface GroupService {
    @GET("user/{user_id}/gruppen")
    suspend fun getGroupsForUser(
        @Path("user_id") userId: String
    ): List<GroupResponse>
}

