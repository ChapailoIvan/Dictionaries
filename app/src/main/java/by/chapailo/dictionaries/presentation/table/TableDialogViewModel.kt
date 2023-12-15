package by.chapailo.dictionaries.presentation.table

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import by.chapailo.dictionaries.domain.DatabaseManager
import by.chapailo.dictionaries.domain.OrderByClause
import by.chapailo.dictionaries.domain.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TableDialogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val databaseManager: DatabaseManager
): ViewModel() {

    private val tableName: String by lazy {
        checkNotNull(savedStateHandle.get<String>("TABLE"))
    }

    private val _stateFlow: MutableStateFlow<TableViewState> = MutableStateFlow(TableViewState())
    val stateFlow: StateFlow<TableViewState> = _stateFlow

    init {
        select(tableName)
    }

    fun select(table: String) {
        _stateFlow.update { TableViewState() }

        var newTableViewState = TableViewState()

        databaseManager.getColumnTypes(tableName).onSuccess { columnTypes ->
            newTableViewState = newTableViewState.copy(columnTypes = columnTypes.toList())
        }

        databaseManager.select(table = tableName, orderByClause = OrderByClause()).onSuccess { rows ->
            val rowsData = mutableListOf<List<String>>()

            rows.forEach { row ->
                rowsData.add(row.data.values.toList())
            }

            newTableViewState = newTableViewState.copy(values = rowsData)
        }

        _stateFlow.update { newTableViewState }
    }

}