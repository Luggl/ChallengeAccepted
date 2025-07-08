package de.thws.challengeaccepted.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import de.thws.challengeaccepted.models.Beitrag
import de.thws.challengeaccepted.models.VoteRequest
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

    fun vote(beitragId: String, vote: String) {
        val oldList = _feed.value ?: return
        // 1. Lokal ändern (damit UI direkt reagiert)
        val newList = oldList.map {
            fun vote(beitragId: String, vote: String, userId: String) {
                val oldList = _feed.value ?: return
                val beitrag = oldList.find { it.beitrag_id == beitragId }
                // Blockiere Voting auf eigene Beiträge:
                if (beitrag != null && beitrag.user_id == userId) {
                    // Optional: Toast/Log
                    return
                }

                // 1. Lokal ändern (damit UI direkt reagiert)
                val newList = oldList.map {
                    if (it.beitrag_id == beitragId) it.copy(user_vote = vote)
                    else it
                }
                _feed.value = newList

                // 2. Dann Backend-Call asynchron (Optional: Fehler-Handling)
                viewModelScope.launch {
                    try {
                        val req = VoteRequest(vote)
                        Log.d("VOTE_REQ", "ID: $beitragId, JSON: ${Gson().toJson(req)}")
                        api.voteBeitrag(beitragId, VoteRequest(vote))
                        fetchFeed()
                    } catch (e: Exception) {
                        Log.e("VOTE", "Error sending vote", e)
                        // Fehler-Handling
                    }
                }
            }
        }
    }
}

