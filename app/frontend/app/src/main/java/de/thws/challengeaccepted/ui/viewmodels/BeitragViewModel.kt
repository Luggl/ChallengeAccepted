package de.thws.challengeaccepted.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.repository.BeitragRepository
import kotlinx.coroutines.launch
import java.io.File

class BeitragViewModel(private val repository: BeitragRepository) : ViewModel() {

    fun uploadBeitrag(
        erfuellungId: String,
        beschreibung: String,
        videoFile: File,
        onResult: (Boolean) -> Unit
    ){
        Log.d("BeitragViewModel", "uploadBeitrag called with erfuellungId=$erfuellungId, beschreibung=$beschreibung")

        viewModelScope.launch {
            val success = repository.uploadBeitrag(erfuellungId, beschreibung, videoFile)
            onResult(success)
        }

    }
}