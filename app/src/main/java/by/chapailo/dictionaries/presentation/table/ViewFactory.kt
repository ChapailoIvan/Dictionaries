package by.chapailo.dictionaries.presentation.table

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import by.chapailo.dictionaries.R
import by.chapailo.dictionaries.common.formatDate
import by.chapailo.dictionaries.domain.ColumnType
import by.chapailo.dictionaries.presentation.Router
import by.chapailo.dictionaries.presentation.dialogs.showDatePickerDialog

fun Fragment.produceView(
    parent: View,
    columnType: ColumnType,
    columnData: String? = null
): View {
    val view = if (columnData == null) {
        produceHeaderView(columnType)
    } else if (columnType.name.startsWith(prefix = "date") && columnType.type == "TEXT") {
        produceDateView(columnType, columnData)
    } else if (columnType.foreignTable != null) {
        produceForeignView(columnType, columnData)
    } else if (columnType.type == "TEXT") {
        produceMultiLineEditText(columnType, columnData)
    } else {
        produceSingleLineEditText(columnType, columnData)
    }

    view.setOnLongClickListener { parent.performLongClick() }

    return view
}

private fun Fragment.produceHeaderView(columnType: ColumnType): View {
    return TextView(requireContext()).apply {
        applyStyle()
        setData(text = columnType.name, tag = columnType.name)

        isEnabled = true
    }
}

private fun Fragment.produceSingleLineEditText(columnType: ColumnType, columnData: String): View {
    return EditText(requireContext()).apply {
        applyStyle()
        setData(text = columnData, tag = columnType.name)

        isSingleLine = true
    }
}

private fun Fragment.produceMultiLineEditText(columnType: ColumnType, columnData: String): View {
    return EditText(requireContext()).apply {
        applyStyle()
        setData(text = columnData, tag = columnType.name)

        isSingleLine = false
    }
}

private fun Fragment.produceDateView(columnType: ColumnType, columnData: String): View {
    return EditText(requireContext()).apply {
        applyStyle()
        setData(text = columnData, tag = columnType.name)

        isFocusable = false

        setOnClickListener {
            showDatePickerDialog(
                isNullable = columnType.isNullable,
                onDatePicked = { year, monthOfYear, dayOfMonth ->
                    setText(formatDate(year, monthOfYear, dayOfMonth))
                },
                onNullPicked = { setText(getString(R.string.string_null)) }
            )
        }
    }
}

private fun Fragment.produceForeignView(columnType: ColumnType, columnData: String): View {
    return EditText(requireContext()).apply {
        applyStyle()
        setData(text = columnData, tag = columnType.name)

        isFocusable = false

        setOnClickListener {
            val dialog = TableDialogFragment.newInstance(
                table = checkNotNull(columnType.foreignTable),
                isNullable = columnType.isNullable
            )

            dialog.onNullPickedListener = { setText(getString(R.string.string_null)) }
            dialog.onDataPickedListener = { rowData ->
                setText(rowData.getValue(checkNotNull(columnType.foreignField)))
            }

            (requireActivity() as? Router)?.navigateTo(dialog)
        }
    }
}

private fun TextView.applyStyle() {
    isEnabled = false
    background = null
    textAlignment = View.TEXT_ALIGNMENT_CENTER

    setPadding(16, 32, 16, 8)
}

private fun TextView.setData(text: String, tag: String) {
    setText(text)
    setTag(tag)
}