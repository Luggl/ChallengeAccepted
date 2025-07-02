package de.thws.challengeaccepted.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import de.thws.challengeaccepted.models.Beitrag
import de.thws.challengeaccepted.network.FeedService
import de.thws.challengeaccepted.network.ApiClient
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val api = ApiClient.getRetrofit(application).create(FeedService::class.java)

    private val _feed = MutableLiveData<List<Beitrag>>()
    val feed: LiveData<List<Beitrag>> = _feed

    fun fetchFeed() {   // <--- Kein token mehr!
        viewModelScope.launch {
            try {
                val response = api.getFeed()   // <--- Kein Argument!
                _feed.value = response.feed.data
            } catch (e: Exception) {
                _feed.value = emptyList()
            }
        }
    }
}
