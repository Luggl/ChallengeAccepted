package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.*
import de.thws.challengeaccepted.data.dao.GruppeDao
import de.thws.challengeaccepted.data.dao.MembershipDao
import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.GroupDashboardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupstatusViewModel(
    private val repository: GroupDashboardRepository,
    private val gruppeDao: GruppeDao,
    private val membershipDao: MembershipDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)

    val groupDetails: StateFlow<Gruppe?> = _groupId.filterNotNull().flatMapLatest {
        gruppeDao.getGruppeByIdAsFlow(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val members: StateFlow<List<User>> = _groupId.filterNotNull().flatMapLatest {
        membershipDao.getMembershipsForGroupAsFlow(it).flatMapLatest { memberships ->
            val userIds = memberships.map { m -> m.userId }
            if (userIds.isNotEmpty()) {
                userDao.getUsersByIdsAsFlow(userIds)
            } else {
                flowOf(emptyList())
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadGroupDetails(groupId: String) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId
        viewModelScope.launch {
            repository.refreshDashboardData(groupId)
        }
    }
}

class GroupstatusViewModelFactory(
    private val repository: GroupDashboardRepository,
    private val gruppeDao: GruppeDao,
    private val membershipDao: MembershipDao,
    private val userDao: UserDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupstatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupstatusViewModel(repository, gruppeDao, membershipDao, userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
