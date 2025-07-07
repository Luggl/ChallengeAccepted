package de.thws.challengeaccepted.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.thws.challengeaccepted.models.StandardChallengeRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.models.ChallengeCreateResponse
import kotlinx.coroutines.launch
import de.thws.challengeaccepted.data.repository.ChallengeRepository


class CreateChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ChallengeRepository(application)

    private val _challengeResult = MutableLiveData<Result<ChallengeCreateResponse>>()
    val challengeResult: LiveData<Result<ChallengeCreateResponse>> = _challengeResult

    fun createChallenge(groupId: String, req: StandardChallengeRequest) {
        viewModelScope.launch {
            try {
                val res = repo.createStandardChallenge(groupId, req)
                _challengeResult.postValue(Result.success(res))
            } catch (e: Exception) {
                _challengeResult.postValue(Result.failure(e))
            }
        }
    }
}
