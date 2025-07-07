package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.*
import de.thws.challengeaccepted.data.dao.ChallengeDao
import de.thws.challengeaccepted.data.dao.MembershipDao
import de.thws.challengeaccepted.data.dao.UserDao
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.GroupDashboardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupDashboardViewModel(
    private val repository: GroupDashboardRepository,
    private val membershipDao: MembershipDao,
    private val challengeDao: ChallengeDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)

    // Flow für die aktive Challenge
    val activeChallenge: StateFlow<Challenge?> = _groupId.filterNotNull().flatMapLatest {
        challengeDao.getActiveChallengeForGroupAsFlow(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Flow für die Mitgliederliste
    val members: StateFlow<List<User>> = _groupId.filterNotNull().flatMapLatest {
        // Zuerst die Membership-Objekte holen, um die User-IDs zu bekommen
        membershipDao.getMembershipsForGroupAsFlow(it).flatMapLatest { memberships ->
            val userIds = memberships.map { m -> m.userId }
            if (userIds.isNotEmpty()) {
                userDao.getUsersByIdsAsFlow(userIds)
            } else {
                flowOf(emptyList()) // Leere Liste, wenn keine Mitglieder
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun loadDashboard(groupId: String) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId
        viewModelScope.launch {
            repository.refreshDashboardData(groupId)
        }
    }
}

// Die zugehörige Factory
class GroupDashboardViewModelFactory(
    private val repository: GroupDashboardRepository,
    private val membershipDao: MembershipDao,
    private val challengeDao: ChallengeDao,
    private val userDao: UserDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupDashboardViewModel(repository, membershipDao, challengeDao, userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}