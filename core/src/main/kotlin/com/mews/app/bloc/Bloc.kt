package com.mews.app.bloc

import kotlinx.coroutines.flow.Flow

interface Bloc<EVENT : Any, STATE : Any> : Flow<STATE>, Emitter<EVENT> {
    /**
     * Returns current state.
     */
    val state: STATE

    /**
     * Call this function to emit a new [EVENT] that should be processed by bloc.
     */
    override suspend fun emit(value: EVENT)

    /**
     * Call this function to emit a new [EVENT] asynchronously within bloc scope.
     */
    fun emitAsync(event: EVENT)

    /**
     * Takes an incoming [event] and emits new [STATE].
     */
    suspend fun Emitter<STATE>.mapEventToState(event: EVENT)

    /**
     * Called whenever [transition] occurs before state is updated.
     */
    suspend fun onTransition(transition: Transition<EVENT, STATE>) {}

    /**
     * Called whenever error is thrown during transition or [mapEventToState].
     */
    suspend fun onError(error: Throwable) {}

    /**
     * Called whenever an [event] is [emit]ted.
     */
    suspend fun onEvent(event: EVENT) {}

    /**
     * Transforms [events] before they go to [mapEventToState]. Override this function to control
     * event processing (e.g. debouncing).
     */
    fun transformEvents(events: Flow<EVENT>): Flow<EVENT> = events

    /**
     * Transforms [transitions] before [onTransition] is called.
     */
    fun transformTransitions(transitions: Flow<Transition<EVENT, STATE>>) = transitions
}
