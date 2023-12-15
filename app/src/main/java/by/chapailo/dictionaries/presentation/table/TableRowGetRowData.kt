package by.chapailo.dictionaries.presentation.table

import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children

fun TableRow.getRowData(): Map<String, String> {
    val data = mutableMapOf<String, String>()

    children.forEach { column ->
        (column as? TextView)?.let {
            val columnName = it.tag as String
            val columnData = it.text.toString()

            data[columnName] = columnData
        }
    }

    return data
}