package com.mews.app.bloc

interface BlocDelegate {
    fun <EVENT : Any> onEvent(event: EVENT)
    fun <EVENT : Any, STATE : Any> onTransition(transition: Transition<EVENT, STATE>)
    fun onError(error: Throwable)
}
