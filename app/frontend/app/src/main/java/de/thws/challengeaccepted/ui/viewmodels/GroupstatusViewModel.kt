package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.*
import de.thws.challengeaccepted.data.dao.GruppeDao
import de.thws.challengeaccepted.data.dao.MembershipDao
import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.GroupRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupStatusViewModel(
    private val repository: GroupRepository
) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)

    val groupDetails: StateFlow<Gruppe?> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getGroupDetails(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val members: StateFlow<List<User>> = _groupId.filterNotNull().flatMapLatest { id ->
        repository.getMembersForGroup(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadGroupDetails(groupId: String) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId
        viewModelScope.launch {
            repository.refreshGroupData(groupId)
        }
    }
}

class GroupStatusViewModelFactory(private val repository: GroupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupStatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupStatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
