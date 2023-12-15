package by.chapailo.dictionaries.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Fragment.collectWithLifecycle(collectBlock: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectBlock()
        }
    }
}

fun Fragment.collectWithLifecycle(dispatcher: CoroutineDispatcher, collectBlock: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch(dispatcher) {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectBlock()
        }
    }
}