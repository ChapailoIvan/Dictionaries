package by.chapailo.dictionaries.domain

@JvmInline
value class DatabaseData(val data: Map<String, String>) {
    val columnNames: List<String>
        get() = data.keys.toList()

    val columnData: List<String>
        get() = data.values.toList()
}

fun Map<String, String>.toDatabaseData(): DatabaseData {
    return DatabaseData(this)
}
