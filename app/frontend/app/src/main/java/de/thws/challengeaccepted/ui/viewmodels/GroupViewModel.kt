package de.thws.challengeaccepted.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.thws.challengeaccepted.data.entities.Gruppe
import de.thws.challengeaccepted.data.repository.GroupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    // Hält die aktuelle Liste der Gruppen bereit für die UI.
    val gruppen: StateFlow<List<Gruppe>> = repository.gruppen
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Beim ersten Starten des ViewModels die Daten vom Server holen.
        refresh()
    }

    // Kann von der UI aufgerufen werden, z.B. für "Swipe-to-Refresh".
    fun refresh() {
        viewModelScope.launch {
            repository.refreshGruppen()
        }
    }
}

// DIESE FACTORY IST NÖTIG, damit das ViewModel mit dem Repository erstellt werden kann.
// Füge diese Klasse in dieselbe Datei "GroupViewModel.kt" ein.
class GroupViewModelFactory(private val repository: GroupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}