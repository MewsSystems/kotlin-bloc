package com.mews.android.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

class TestBlocDelegate : BlocDelegate {
    private val _events = mutableListOf<Any>()
    private val _transitions = mutableListOf<Any>()
    private val _errors = mutableListOf<Throwable>()

    val events: List<Any> get() = _events.toList()
    val transitions: List<Any> get() = _transitions.toList()
    val errors: List<Any> get() = _errors.toList()

    override fun <EVENT : Any> onEvent(event: EVENT) {
        _events.add(event)
    }

    override fun <EVENT : Any, STATE : Any> onTransition(transition: Transition<EVENT, STATE>) {
        _transitions.add(transition)
    }

    override fun onError(error: Throwable) {
        _errors.add(error)
    }
}

fun runBlockingAndCancelScope(bloc: suspend CoroutineScope.() -> Unit) = runBlocking {
    val scope = CoroutineScope(coroutineContext + Job())
    bloc(scope)
    scope.cancel()
}
