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

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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
        // NICHT lokal updaten! Nur Backend-Call + danach Refresh
        viewModelScope.launch {
            try {
                val req = VoteRequest(vote)
                Log.d("VOTE_REQ", "ID: $beitragId, JSON: ${Gson().toJson(req)}")
                api.voteBeitrag(beitragId, VoteRequest(vote))
                // Feed nach Voting NEU vom Backend holen!
                fetchFeed()
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
