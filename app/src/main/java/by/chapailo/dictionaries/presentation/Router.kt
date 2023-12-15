package by.chapailo.dictionaries.presentation

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

interface Router {
    fun navigateTo(dialogFragment: DialogFragment)
    fun navigateTo(fragment: Fragment)
    fun navigateBack()
}