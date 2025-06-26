package de.thws.challengeaccepted.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.App
import de.thws.challengeaccepted.data.entities.User
import de.thws.challengeaccepted.data.repository.UserRepository
import kotlinx.coroutines.launch

class  UserViewModel(application: Application) : AndroidViewModel(application) {

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
}