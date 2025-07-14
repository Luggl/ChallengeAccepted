package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.entities.Aufgabe
import de.thws.challengeaccepted.data.entities.BeitragEntity
import de.thws.challengeaccepted.data.entities.Challenge
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.GroupRepository
import de.thws.challengeaccepted.models.VoteRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    private val _groupId = MutableStateFlow<String?>(null)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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

    // --- NEU: Aufgabe + Timer-Logik ---

    // StateFlow für die offene Aufgabe in der aktuellen Gruppe
    private val _openTask = MutableStateFlow<Aufgabe?>(null)
    val openTask: StateFlow<Aufgabe?> = _openTask

    // StateFlow für den verbleibenden Countdown in Sekunden
    private val _remainingTime = MutableStateFlow<Long?>(null)
    val remainingTime: StateFlow<Long?> = _remainingTime

    private var countdownJob: Job? = null

    /**
     * Lädt alle Gruppendaten und die offene Aufgabe (mit Timer) für das Dashboard.
     * userId: Muss aus SharedPreferences oder sonst wie bereitgestellt werden.
     */
    fun loadGroupDataWithTask(groupId: String, userId: String) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId

        // Lade normale Gruppendaten
        viewModelScope.launch {
            repository.refreshGroupData(groupId)
        }

        // Lade die offene Aufgabe + starte Timer
        viewModelScope.launch {
            val task = repository.getOpenTaskForGroup(groupId, userId)
            _openTask.value = task

            // Starte/Reset Timer nur, wenn eine Aufgabe da ist und Deadline gesetzt ist
            countdownJob?.cancel()
            if (task?.deadline != null) {
                startCountdown(task.deadline)
            } else {
                _remainingTime.value = null
            }
        }
    }

    // Nur Aufgaben/Timer neu laden (ohne alle Gruppendaten)
    fun reloadTaskOnly(groupId: String, userId: String) {
        viewModelScope.launch {
            val task = repository.getOpenTaskForGroup(groupId, userId)
            _openTask.value = task

            countdownJob?.cancel()
            if (task?.deadline != null) {
                startCountdown(task.deadline)
            } else {
                _remainingTime.value = null
            }
        }
    }

    private fun startCountdown(deadlineMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val remaining = (deadlineMillis - now) / 1000 // Sekunden
                _remainingTime.value = remaining.coerceAtLeast(0)
                if (remaining <= 0) break
                delay(1000)
            }
        }
    }

    // --- Bisherige Standard-Methoden ---
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
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 400) {
                    _errorMessage.value = "Du hast schon gevoted!"
                } else {
                    _errorMessage.value = "Fehler: ${e.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ein Fehler ist aufgetreten"
            }
        }
    }
    fun clearError() {
        _errorMessage.value = null
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
