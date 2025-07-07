package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChallengeService {
    // NEU: Holt alle Challenges für eine bestimmte Gruppe
    @GET("challenges")
    suspend fun getChallengesForGroup(@Query("group_id") groupId: String): ChallengeListResponse

    // --- Bestehende Funktionen zum Erstellen ---
    @POST("challengestandard")
    suspend fun createStandardChallenge(
        @Query("group_id") groupId: String,
        @Body body: StandardChallengeRequest
    ): ChallengeCreateResponse

    @POST("challengesurvival")
    suspend fun createSurvivalChallenge(
        @Query("group_id") groupId: String,
        @Body body: SurvivalChallengeRequest
    ): ChallengeCreateResponse
}