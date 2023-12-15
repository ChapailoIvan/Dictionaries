package by.chapailo.dictionaries.presentation.dialogs

import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import by.chapailo.dictionaries.R
import java.util.Calendar

fun Fragment.showDatePickerDialog(
    isNullable: Boolean,
    onDatePicked: (year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit,
    onNullPicked: () -> Unit
) {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val month = Calendar.getInstance().get(Calendar.MONTH)
    val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, monthOfYear, dayOfMonth ->
            onDatePicked(year, monthOfYear, dayOfMonth)
        },
        year,
        month,
        dayOfMonth
    )

    if (isNullable) {
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.dialog_date_picker_button_null)) { dialog, _ ->
            onNullPicked()
            dialog.dismiss()
        }
    }

    datePickerDialog.show()
}
