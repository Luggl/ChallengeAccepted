package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.ChallengeCreateResponse
import de.thws.challengeaccepted.models.StandardChallengeRequest
import de.thws.challengeaccepted.models.SurvivalChallengeRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ChallengeService {
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
