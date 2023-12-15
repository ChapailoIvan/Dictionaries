package by.chapailo.dictionaries.presentation.table

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import by.chapailo.dictionaries.domain.DatabaseManager
import by.chapailo.dictionaries.domain.OrderByClause
import by.chapailo.dictionaries.domain.onError
import by.chapailo.dictionaries.domain.onSuccess
import by.chapailo.dictionaries.domain.toDatabaseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<TableViewState> = MutableStateFlow(TableViewState())
    val stateFlow: StateFlow<TableViewState> = _stateFlow.asStateFlow()

    private val eventChannel: Channel<TableFragmentEvent> = Channel()
    val eventFlow: Flow<TableFragmentEvent> = eventChannel.receiveAsFlow()

    private val tableName: String by lazy { checkNotNull(savedStateHandle.get<String>("TABLE")) }

    private var orderByClause: OrderByClause = OrderByClause()

    init {
        select()
        getTableInfo(tableName)
    }

    fun getTableInfo(table: String) {
        databaseManager.getColumnTypes(table).onSuccess { columnTypes ->
            columnTypes.forEach {  columnType ->
                Log.d("ViewModel", columnType.toString())
            }
        }
    }

    fun select(orderByColumn: String? = null) {
        orderByClause = orderByClause.nextClause(orderByColumn)
        _stateFlow.update { TableViewState() }

        var newTableViewState = TableViewState(
            orderBy = orderByClause.column ?: "",
            orderType = orderByClause.option ?: OrderByClause.OrderType.ASC
        )

        databaseManager.getColumnTypes(tableName).onSuccess { columnTypes ->
            newTableViewState = newTableViewState.copy(columnTypes = columnTypes.toList())
        }

        databaseManager.select(table = tableName, orderByClause = orderByClause).onSuccess { rows ->
            val rowsData = mutableListOf<List<String>>()

            rows.forEach { row ->
                rowsData.add(row.data.values.toList())
            }

            newTableViewState = newTableViewState.copy(values = rowsData)
        }

        _stateFlow.update { newTableViewState }
    }

    fun insert(data: Map<String, String>) {
        databaseManager.insert(
            table = tableName,
            data = data.toDatabaseData()
        ).onSuccess {
            eventChannel.trySend(TableFragmentEvent.ShowSnackbar(message = "Успешно"))
        }.onError {
            eventChannel.trySend(TableFragmentEvent.ShowErrorDialog(message = it.message.toString()))
        }
    }

    fun delete(data: Map<String, String>) {
        databaseManager.delete(
            table = tableName,
            data = data.toDatabaseData()
        ).onSuccess {
            eventChannel.trySend(TableFragmentEvent.ShowSnackbar(message = "Успешно"))
        }.onError {
            eventChannel.trySend(TableFragmentEvent.ShowErrorDialog(message = it.message.toString()))
        }
    }

    fun update(rowIndex: Int, data: Map<String, String>) {
        val (columnTypes, values) = _stateFlow.value
        val headers = columnTypes.map { columnType -> columnType.name }

        val oldData = values.map { rowData -> headers.zip(rowData).toMap() }[rowIndex]
        if (oldData == data)
            return

        databaseManager.update(
            table = tableName,
            oldData = oldData.toDatabaseData(),
            newData = data.toDatabaseData()
        ).onSuccess {
            eventChannel.trySend(TableFragmentEvent.ShowSnackbar(message = "Успешно"))
        }.onError {
            eventChannel.trySend(TableFragmentEvent.ShowErrorDialog(message = it.message.toString()))
        }
    }

}