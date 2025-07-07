package de.thws.challengeaccepted.data.repository

import android.content.Context
import de.thws.challengeaccepted.models.ChallengeCreateResponse
import de.thws.challengeaccepted.models.StandardChallengeRequest
import de.thws.challengeaccepted.models.SurvivalChallengeRequest
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.ChallengeService

class ChallengeRepository(context: Context) {
    private val service = ApiClient.getRetrofit(context).create(ChallengeService::class.java)

    suspend fun createStandardChallenge(groupId: String, request: StandardChallengeRequest): ChallengeCreateResponse {
        return service.createStandardChallenge(groupId, request)
    }
    suspend fun createSurvivalChallenge(groupId: String, request: SurvivalChallengeRequest): ChallengeCreateResponse {
        return service.createSurvivalChallenge(groupId, request)
    }
}
