package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.entities.BeitragEntity
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.GroupRepository
import de.thws.challengeaccepted.models.VoteRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)

    val feed: StateFlow<List<BeitragEntity>> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getFeedForGroup(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChallenge: StateFlow<Challenge?> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getActiveChallenge(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val groupDetails: StateFlow<Gruppe?> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getGroupDetails(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val members: StateFlow<List<User>> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getMembersForGroup(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gruppen: StateFlow<List<Gruppe>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadGroupData(groupId: String) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId
        viewModelScope.launch {
            repository.refreshGroupData(groupId)
        }
    }
    fun loadGroupOverview() {
        viewModelScope.launch {
            repository.refreshGroupOverview() // Holt und speichert ALLE Gruppen!
        }
    }
    fun vote(beitragId: String, vote: String, userId: String, groupId: String) {
        viewModelScope.launch {
            try {
                val feedList = feed.value
                val beitrag = feedList.find { it.beitrag_Id == beitragId }
                if (beitrag != null && beitrag.user_id == userId) {
                    // Optional: Fehler/Toast
                    return@launch
                }
                repository.voteBeitragGroup(beitragId, VoteRequest(vote))
                repository.refreshGroupData(groupId)
            } catch (e: Exception) {
                // Fehlerbehandlung
            }
        }
    }
}

// Factory
class GroupViewModelFactory(private val repository: GroupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
