package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.CreateGroupResponse
import de.thws.challengeaccepted.models.GroupCreateRequest
import de.thws.challengeaccepted.models.GroupOverviewResponse
import de.thws.challengeaccepted.models.GroupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.POST


interface GroupService {

    @POST("group")
    suspend fun createGroup(@Body request: GroupCreateRequest): CreateGroupResponse

    @GET("groups")
    suspend fun getGroupOverview(): GroupOverviewResponse
}

