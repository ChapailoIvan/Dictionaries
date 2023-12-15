package by.chapailo.dictionaries.data

import android.content.Context
import android.content.SharedPreferences
import by.chapailo.dictionaries.domain.SharedPreferencesManager

class SharedPreferencesManagerImpl(
    appContext: Context
): SharedPreferencesManager {

    companion object {
        private const val SHARED_PREFERENCES = "DEFAULT"
        private const val IS_WELCOME_DIALOG_SHOWN_KEY = "IS_WELCOME_DIALOG_SHOWN"
    }

    private val sharedPreferences =
        appContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

    override fun isWelcomeDialogShown(): Boolean {
        return sharedPreferences.getBoolean(IS_WELCOME_DIALOG_SHOWN_KEY, false)
    }

    override fun setWelcomeDialogShown() {
        sharedPreferences.edit()
            .putBoolean(IS_WELCOME_DIALOG_SHOWN_KEY, true)
            .apply()
    }
}