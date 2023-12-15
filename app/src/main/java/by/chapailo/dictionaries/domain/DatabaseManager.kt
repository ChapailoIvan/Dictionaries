package by.chapailo.dictionaries.domain

interface DatabaseManager {

    fun getTables(): DatabaseResult<List<String>>
    fun getColumnTypes(table: String): DatabaseResult<List<ColumnType>>

    fun select(table: String, orderByClause: OrderByClause): DatabaseResult<List<DatabaseData>>
    fun insert(table: String, data: DatabaseData): DatabaseResult<Unit>
    fun update(table: String, oldData: DatabaseData, newData: DatabaseData): DatabaseResult<Unit>
    fun delete(table: String, data: DatabaseData): DatabaseResult<Unit>

    fun execute(query: String): DatabaseResult<Any>

    fun prepopulateData()

}