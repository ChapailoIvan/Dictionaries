package by.chapailo.dictionaries.presentation.tables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.chapailo.dictionaries.domain.DatabaseManager
import by.chapailo.dictionaries.domain.SharedPreferencesManager
import by.chapailo.dictionaries.domain.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TablesViewModel @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val sharedPreferencesManager: SharedPreferencesManager
): ViewModel() {

    private val _stateFlow: MutableStateFlow<TablesViewState> =
        MutableStateFlow(TablesViewState())
    val stateFlow: StateFlow<TablesViewState> = _stateFlow.asStateFlow()

    private val eventChannel: Channel<TablesFragmentEvent> = Channel()
    val eventFlow: Flow<TablesFragmentEvent> = eventChannel.receiveAsFlow()

    init {
        checkSharedPreferences()
    }

    fun loadTables() = viewModelScope.launch(Dispatchers.IO) {
        databaseManager.getTables().onSuccess {  tables ->
            _stateFlow.update { state ->
                state.copy(tables = tables)
            }
        }
    }

    private fun checkSharedPreferences() = viewModelScope.launch {
        val isWelcomeDialogShown = sharedPreferencesManager.isWelcomeDialogShown()
        if (!isWelcomeDialogShown) {
            eventChannel.send(TablesFragmentEvent.ShowWelcomeDialog)
            sharedPreferencesManager.setWelcomeDialogShown()
            prepopulateDatabaseData()
        } else {
            loadTables()
        }
    }

    private fun prepopulateDatabaseData() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseManager.prepopulateData()
        }
    }

}