package de.thws.challengeaccepted.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.thws.challengeaccepted.data.repository.FeedRepository
import de.thws.challengeaccepted.models.GroupFeedItem
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.repository.GroupFeedRepository
import kotlinx.coroutines.launch

class GroupFeedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GroupFeedRepository(application)

    private val _feed = MutableLiveData<List<GroupFeedItem>>()
    val feed: LiveData<List<GroupFeedItem>> = _feed

    fun loadFeed(groupId: String) {
        viewModelScope.launch {
            try {
                _feed.value = repository.getGroupFeed(groupId)
            } catch (e: Exception) {
                _feed.value = emptyList()
            }
        }
    }
}