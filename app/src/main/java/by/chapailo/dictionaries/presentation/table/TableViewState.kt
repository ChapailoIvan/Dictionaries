package by.chapailo.dictionaries.presentation.table

import by.chapailo.dictionaries.domain.ColumnType
import by.chapailo.dictionaries.domain.OrderByClause

data class TableViewState(
    val columnTypes: List<ColumnType> = emptyList(),
    val values: List<List<String>> = emptyList(),
    val orderBy: String = "",
    val orderType: OrderByClause.OrderType = OrderByClause.OrderType.ASC
)