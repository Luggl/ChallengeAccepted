package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.*
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.repository.ChallengeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChallengeViewModel(private val repository: ChallengeRepository) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)

    // Wandelt den kalten Flow vom Repository in einen heißen StateFlow um
    val challenges: StateFlow<List<Challenge>> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getChallengesForGroup(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Wird von der Activity aufgerufen, um den Prozess zu starten
    fun loadChallengesForGroup(groupId: String) {
        _groupId.value = groupId
        viewModelScope.launch {
            repository.refreshChallenges(groupId)
        }
    }
}

// Die zugehörige Factory
class ChallengeViewModelFactory(
    private val repository: ChallengeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChallengeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChallengeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}