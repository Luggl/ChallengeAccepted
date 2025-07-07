package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.repository.ChallengeRepository
import de.thws.challengeaccepted.models.ChallengeCreateResponse
import de.thws.challengeaccepted.models.StandardChallengeRequest
import de.thws.challengeaccepted.models.SurvivalChallengeRequest
import kotlinx.coroutines.launch

class CreateChallengeViewModel(
    private val repository: ChallengeRepository // Erhält das Repository als Abhängigkeit
) : ViewModel() {

    private val _challengeResult = MutableLiveData<Result<ChallengeCreateResponse>>()
    val challengeResult: LiveData<Result<ChallengeCreateResponse>> = _challengeResult

    // Die Logik hier drin bleibt gleich, sie ruft nur die Repository-Funktion auf.
    fun createStandardChallenge(groupId: String, request: StandardChallengeRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createStandardChallenge(groupId, request)
                _challengeResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _challengeResult.postValue(Result.failure(e))
            }
        }
    }

    fun createSurvivalChallenge(groupId: String, request: SurvivalChallengeRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createSurvivalChallenge(groupId, request)
                _challengeResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _challengeResult.postValue(Result.failure(e))
            }
        }
    }
}

// DIESE FACTORY IST NÖTIG, um das ViewModel mit seinem Repository zu erstellen.
class CreateChallengeViewModelFactory(
    private val repository: ChallengeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateChallengeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateChallengeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}