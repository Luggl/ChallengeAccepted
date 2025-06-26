package de.thws.challengeaccepted.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.App
import de.thws.challengeaccepted.data.repository.GroupRepository
import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.GroupService
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GroupRepository(
        ApiClient.retrofit.create(GroupService::class.java)
    )

    fun getGroups(userId: String, token: String, onResult: (List<GroupResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val groups = repository.getGroupsForUser(userId, token)
                onResult(groups)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }
}
