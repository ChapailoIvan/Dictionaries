package by.chapailo.dictionaries.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatDate(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day)

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}