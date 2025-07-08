package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.CreateGroupResponse
import de.thws.challengeaccepted.models.GroupCreateRequest
import de.thws.challengeaccepted.models.GroupOverviewResponse
import de.thws.challengeaccepted.models.GroupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import de.thws.challengeaccepted.models.*


interface GroupService {

    // KORRIGIERT: Der Rückgabetyp ist jetzt die neue Top-Level-Klasse
    @GET("groupfeed")
    suspend fun getGroupFeed(@Query("group_id") groupId: String): GroupFeedApiResponse

    @POST("group")
    suspend fun createGroup(@Body request: GroupCreateRequest): CreateGroupResponse

    @GET("groups")
    suspend fun getGroupOverview(): GroupOverviewResponse

    @POST("vote")
    suspend fun voteBeitragGroup(
        @Query("beitrag_id") beitragId: String,
        @Body voteRequest: VoteRequest
    )

}
