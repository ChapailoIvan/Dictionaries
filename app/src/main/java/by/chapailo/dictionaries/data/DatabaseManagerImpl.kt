package by.chapailo.dictionaries.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import by.chapailo.dictionaries.domain.ColumnType
import by.chapailo.dictionaries.domain.DatabaseData
import by.chapailo.dictionaries.domain.DatabaseManager
import by.chapailo.dictionaries.domain.DatabaseResult
import by.chapailo.dictionaries.domain.MutableColumnType
import by.chapailo.dictionaries.domain.OrderByClause
import by.chapailo.dictionaries.domain.safeQuery
import by.chapailo.dictionaries.domain.toColumnType
import by.chapailo.dictionaries.domain.toDatabaseData


class DatabaseManagerImpl(
    appContext: Context
) : DatabaseManager, SQLiteOpenHelper(appContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "DatabaseManagerImpl"

        private const val DATABASE_NAME = "database.db"
        private const val DATABASE_VERSION = 1

        private const val DELETED_COLUMN_NAME = "__deleted"
        private const val NULL = "null"
    }

    override fun getTables(): DatabaseResult<List<String>> = safeQuery {
        val product = mutableListOf<String>()

        readableDatabase
            .rawQuery("SELECT name FROM sqlite_master WHERE type='table';").use { cursor ->
                val tableNameIndex = cursor.getColumnIndex("name")

                while (cursor.moveToNext()) {
                    val tableName = cursor.getString(tableNameIndex)
                    product.add(tableName)
                }
            }

        return@safeQuery product.filter { tableName -> tableName != "android_metadata" }
    }

    override fun getColumnTypes(table: String): DatabaseResult<List<ColumnType>> = safeQuery {
        val mutableColumnTypes = mutableListOf<MutableColumnType>()

        readableDatabase.rawQuery("PRAGMA table_info($table)").use { cursor ->
            val columnNameIndex = cursor.getColumnIndex("name")
            val typeIndex = cursor.getColumnIndex("type")
            val notNullIndex = cursor.getColumnIndex("notnull")
            val primaryKeyIndex = cursor.getColumnIndex("pk")

            while (cursor.moveToNext()) {
                val columnType = MutableColumnType(
                    name = cursor.getString(columnNameIndex),
                    type = cursor.getString(typeIndex),
                    isNullable = cursor.getInt(notNullIndex) == 0 && cursor.getInt(primaryKeyIndex) == 0
                )

                mutableColumnTypes.add(columnType)
            }
        }

        readableDatabase.rawQuery("PRAGMA foreign_key_list($table)").use { cursor ->
            val tableColumnIndex = cursor.getColumnIndex("table")
            val fromColumnIndex = cursor.getColumnIndex("from")
            val toColumnIndex = cursor.getColumnIndex("to")

            while (cursor.moveToNext()) {
                val from = cursor.getString(fromColumnIndex)
                val to = cursor.getString(toColumnIndex)
                val table = cursor.getString(tableColumnIndex)

                mutableColumnTypes.find { columnType -> columnType.name == from }?.apply {
                    foreignTable = table
                    foreignField = to
                }
            }
        }

        return@safeQuery mutableColumnTypes
            .filter { mutableColumnType -> mutableColumnType.name != DELETED_COLUMN_NAME }
            .map { mutableColumnType -> mutableColumnType.toColumnType() }
    }

    override fun select(
        table: String,
        orderByClause: OrderByClause
    ): DatabaseResult<List<DatabaseData>> = safeQuery {
        unsafeEnsureTableFormat(table)

        val product = mutableListOf<DatabaseData>()

        readableDatabase
            .rawQuery("SELECT * FROM $table WHERE $DELETED_COLUMN_NAME = 0 $orderByClause")
            .use { cursor ->
                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()

                    IntRange(0, cursor.columnCount - 1)
                        .filter { columnIndex ->
                            columnIndex != cursor.getColumnIndex(DELETED_COLUMN_NAME)
                        }
                        .forEach { columnIndex ->
                            rowData[cursor.getColumnName(columnIndex)] =
                                when (cursor.getType(columnIndex)) {
                                    Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(columnIndex).toString()
                                    Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(columnIndex).toString()
                                    Cursor.FIELD_TYPE_STRING -> cursor.getString(columnIndex)
                                    else -> NULL
                                }
                        }

                    product.add(rowData.toDatabaseData())
                }
            }

        return@safeQuery product
    }

    override fun insert(table: String, data: DatabaseData): DatabaseResult<Unit> = safeQuery {
        val values = ContentValues().apply { put(data) }
        writableDatabase
            .insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_NONE)
    }

    override fun update(
        table: String,
        oldData: DatabaseData,
        newData: DatabaseData
    ): DatabaseResult<Unit> = safeQuery {
        val (whereClause, whereArgs) = oldData.toWhereClause()
        val values = ContentValues().apply { put(newData) }

        writableDatabase.update(table, values, whereClause, whereArgs)
    }

    override fun delete(table: String, data: DatabaseData): DatabaseResult<Unit> = safeQuery {
        val (whereClause, whereArgs) = data.toWhereClause()
        val values = ContentValues().apply {
            put(DELETED_COLUMN_NAME, 1)
        }

        writableDatabase.update(table, values, whereClause, whereArgs)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "$TAG::onCreate")
        createDatabaseTables(database = db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "$TAG::onUpgrade::$oldVersion to $newVersion")
    }

    private fun SQLiteDatabase.rawQuery(query: String): Cursor {
        return rawQuery(query, null)
    }

    private fun unsafeEnsureTableFormat(table: String) {
        val isCorrectTableFormat = unsafeCheckTableFormat(table)
        if (!isCorrectTableFormat) {
            unsafeFormatTable(table)
        }
    }

    private fun unsafeCheckTableFormat(table: String): Boolean {
        readableDatabase.rawQuery("SELECT * FROM $table").use { cursor ->
            cursor.moveToFirst()
            val auxiliaryDeleteColumnIndex = cursor.getColumnIndex(DELETED_COLUMN_NAME)
            return auxiliaryDeleteColumnIndex != -1
        }
    }

    private fun unsafeFormatTable(table: String) {
        writableDatabase
            .execSQL("ALTER TABLE $table ADD COLUMN $DELETED_COLUMN_NAME INTEGER NOT NULL DEFAULT 0")
    }

    override fun execute(query: String): DatabaseResult<Any> = safeQuery {
        writableDatabase.execSQL(query)
    }

    override fun prepopulateData() {
       prepopulateData(database = writableDatabase)
    }

    private fun DatabaseData.toWhereClause(): Pair<String, Array<String>> {
        val whereClause = this.data.map { (columnName, columnData) ->
            if (columnData.equals(NULL, true)) {
                "$columnName IS NULL"
            } else {
                "$columnName = ?"
            }
        }.joinToString(separator = " AND ")

        val whereArgs = this.columnData
            .filter { columnData -> !columnData.equals(NULL, true) }
            .toTypedArray()

        return whereClause to whereArgs
    }

    private fun ContentValues.put(data: DatabaseData) {
        data.data.forEach { (columnName, columnData) ->
            if (columnData.equals(NULL, true)) {
                putNull(columnName)
            } else {
                put(columnName, columnData)
            }
        }
    }

}