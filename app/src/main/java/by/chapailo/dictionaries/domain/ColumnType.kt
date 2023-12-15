package by.chapailo.dictionaries.domain

class MutableColumnType(
    var name: String,
    var type: String,
    var isNullable: Boolean,
    var foreignTable: String? = null,
    var foreignField: String? = null
)

data class ColumnType(
    val name: String,
    val type: String,
    val isNullable: Boolean,
    val foreignTable: String? = null,
    val foreignField: String? = null
)

fun MutableColumnType.toColumnType(): ColumnType {
    return ColumnType(
        name = name,
        type = type,
        isNullable = isNullable,
        foreignTable = foreignTable,
        foreignField = foreignField
    )
}