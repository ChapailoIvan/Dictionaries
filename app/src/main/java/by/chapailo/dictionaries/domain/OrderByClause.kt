package by.chapailo.dictionaries.domain

data class OrderByClause(
    val column: String? = null,
    val option: OrderType? = null
) {

    enum class OrderType {
        ASC, DESC
    }

    fun nextClause(column: String?): OrderByClause {
        return when (option) {
            null -> copy(column = column, option = OrderType.ASC)
            OrderType.ASC -> copy(option = OrderType.DESC)
            OrderType.DESC -> OrderByClause()
        }
    }

    override fun toString(): String {
        return if (column == null) ""
        else if (option == null) "ORDER BY $column"
        else "ORDER BY $column $option"
    }

}
