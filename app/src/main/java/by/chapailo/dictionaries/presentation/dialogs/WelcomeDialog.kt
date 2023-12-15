package by.chapailo.dictionaries.presentation.dialogs

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import by.chapailo.dictionaries.R

fun Fragment.showWelcomeDialog(
    onPositiveButtonClick: () -> Unit
) {
    AlertDialog.Builder(requireContext())
        .setMessage(R.string.dialog_welcome_text)
        .setPositiveButton(R.string.dialog_welcome_button_continue) { dialog, _ ->
            onPositiveButtonClick()
            dialog.dismiss()
        }
        .create()
        .show()
}