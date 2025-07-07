package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.*
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.UserRepository
import de.thws.challengeaccepted.network.UserService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() { // Das separate apiService wird nicht mehr benötigt

    private val _userId = MutableStateFlow<String?>(null)

    val user: StateFlow<User?> = _userId.filterNotNull().flatMapLatest { id ->
        repository.getUserFlow(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _kalender = MutableLiveData<Map<String, String>>()
    val kalender: LiveData<Map<String, String>> = _kalender

    fun loadInitialData(userId: String) {
        if (_userId.value == userId) return
        _userId.value = userId

        viewModelScope.launch {
            // GEÄNDERT: Die Funktion gibt jetzt die Kalenderdaten direkt zurück
            val calendarData = repository.refreshUser()
            // Die LiveData werden mit dem Ergebnis aktualisiert
            _kalender.postValue(calendarData)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteCurrentUser(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteCurrentUser(userId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unbekannter Fehler")
            }
        }
    }
}

// Die Factory muss jetzt auch angepasst werden, da das ViewModel nur noch eine Abhängigkeit hat
class UserViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}