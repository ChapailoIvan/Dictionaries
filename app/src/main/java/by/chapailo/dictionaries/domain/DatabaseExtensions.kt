package by.chapailo.dictionaries.domain

import android.util.Log

fun <T> safeQuery(block: () -> T): DatabaseResult<T> {
    return try {
        val result = block()
        DatabaseResult.Success(data = result)
    } catch (throwable: Throwable) {
        Log.e("SafeQuery", throwable.message.toString())
        DatabaseResult.Error(throwable = throwable)
    }
}

