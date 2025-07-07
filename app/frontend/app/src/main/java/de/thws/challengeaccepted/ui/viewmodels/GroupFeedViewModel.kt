package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.*
import de.thws.challengeaccepted.data.entities.BeitragEntity
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.repository.GroupFeedRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupFeedViewModel(private val repository: GroupFeedRepository) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)

    // Flow für den Feed
    val feed: StateFlow<List<BeitragEntity>> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getFeedForGroup(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow für die aktive Challenge
    val activeChallenge: StateFlow<Challenge?> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getActiveChallenge(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Startet das Laden aller Daten für die angegebene Gruppen-ID
    fun loadDashboardData(groupId: String) {
        if (_groupId.value == groupId) return // Nicht erneut laden, wenn schon aktiv
        _groupId.value = groupId
        viewModelScope.launch {
            repository.refreshAllDashboardData(groupId)
        }
    }
}

// Die zugehörige Factory
class GroupFeedViewModelFactory(private val repository: GroupFeedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupFeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupFeedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
