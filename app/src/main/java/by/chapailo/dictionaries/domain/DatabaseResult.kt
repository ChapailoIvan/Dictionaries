package by.chapailo.dictionaries.domain

sealed class DatabaseResult<T> {
    class Success<T>(val data: T): DatabaseResult<T>()
    class Error<T>(val data: T? = null, val throwable: Throwable): DatabaseResult<T>()
}

fun <T> DatabaseResult<T>.onSuccess(block: (T) -> Unit): DatabaseResult<T> {
    when(this) {
        is DatabaseResult.Success -> {
            block(this.data)
        }
        else -> { }
    }

    return this
}

fun <T> DatabaseResult<T>.onError(block: (Throwable) -> Unit): DatabaseResult<T> {
    when(this) {
        is DatabaseResult.Error -> {
            block(throwable)
        }
        else -> { }
    }

    return this
}