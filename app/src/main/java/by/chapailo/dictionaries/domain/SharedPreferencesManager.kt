package by.chapailo.dictionaries.domain

interface SharedPreferencesManager {
    fun isWelcomeDialogShown(): Boolean
    fun setWelcomeDialogShown()
}