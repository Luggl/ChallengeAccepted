package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.repository.BeitragRepository
import kotlinx.coroutines.launch
import java.io.File

class BeitragViewModel(private val repository: BeitragRepository) : ViewModel() {

    fun uploadBeitrag(
        userId: String,
        erfuellungId: String,
        beschreibung: String,
        videoFile: File,
        onResult: (Boolean) -> Unit
    ){
        viewModelScope.launch {
            val success = repository.uploadBeitrag(userId, erfuellungId, beschreibung, videoFile)
            onResult(success)
        }

    }
}