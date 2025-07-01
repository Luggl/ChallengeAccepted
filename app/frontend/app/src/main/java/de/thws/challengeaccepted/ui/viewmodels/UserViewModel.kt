package de.thws.challengeaccepted.ui.viewmodels
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.App
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.UserRepository
import kotlinx.coroutines.launch

class  UserViewModel(application: Application) : AndroidViewModel(application) {
    private val api = ApiClient.getRetrofit(getApplication()).create(UserService::class.java)
    private val repository = UserRepository(App.Companion.database.userDao())

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun getUser(userId: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUser(userId)
            onResult(user)
        }
    }
    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    private val _kalender = MutableLiveData<Map<String, String>>()
    val kalender: LiveData<Map<String, String>> = _kalender

    fun fetchUserAndCalendar() {
        viewModelScope.launch {
            try {
                val response = api.getUser()
                _kalender.value = response.user.Kalender
            } catch (e: Exception) {
                // Fehlerhandling (z.B. Log oder Toast)
                _kalender.value = emptyMap() // oder Fehlerstatus setzen
            }
        }
    }
}