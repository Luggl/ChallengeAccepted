package de.thws.challengeaccepted.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.thws.challengeaccepted.data.repository.BeitragRepository

class BeitragViewModelFactory(
    private val repository: BeitragRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(BeitragViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeitragViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}