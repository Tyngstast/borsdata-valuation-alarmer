package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.FlowAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow

actual abstract class ViewModel {

    actual val viewModelScope = MainScope()

    /**
     * Override this to do any cleanup immediately before the internal [kotlinx.coroutines.CoroutineScope]
     * children are cancelled in [clear]
     */
    protected actual open fun onCleared() {
        // override to use
    }

    /**
     * Cancels all children of the [kotlinx.coroutines.CoroutineScope].
     */
    fun clear() {
        onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }
}

abstract class CallbackViewModel {

    protected abstract val viewModel: ViewModel

    /**
     * Create a [FlowAdapter] from this [Flow] to make it easier to interact with from Swift.
     */
    fun <T : Any> Flow<T>.asCallbacks() = FlowAdapter(viewModel.viewModelScope, this)

    @Suppress("Unused") // Called from Swift
    fun clear() = viewModel.clear()
}
