package by.chapailo.dictionaries.presentation.table

sealed interface TableFragmentEvent {
    class ShowSnackbar(val message: String): TableFragmentEvent
    class ShowErrorDialog(val message: String): TableFragmentEvent
}