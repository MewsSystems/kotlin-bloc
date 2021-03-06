package com.mews.app.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseBloc<EVENT : Any, STATE : Any>(private val scope: CoroutineScope) : Bloc<EVENT, STATE> {
    protected abstract val initialState: STATE

    private val stateFlow by lazy { MutableStateFlow(initialState) }

    override val state: STATE get() = stateFlow.value

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<STATE>) = stateFlow.collect(collector)

    private val eventsFlow = MutableSharedFlow<EVENT>().also { events ->
        transformEvents(events)
            .flatMapConcat { event ->
                try {
                    mapEventToState(event)
                        .map { Transition(stateFlow.value, event, it) }
                        .catch { doOnError(it) }
                        .let(::transformTransitions)
                } catch (e: Exception) {
                    doOnError(e)
                    flow<Transition<EVENT, STATE>> { }
                }
            }
            .onEach { transition ->
                if (transition.currentState == transition.nextState) return@onEach
                try {
                    doOnTransition(transition)
                    stateFlow.value = transition.nextState
                } catch (e: Throwable) {
                    doOnError(e)
                }
            }
            .launchIn(scope)
    }

    override fun add(value: EVENT) {
        scope.launch {
            try {
                doOnEvent(value)
                eventsFlow.emit(value)
            } catch (e: Throwable) {
                doOnError(e)
            }
        }
    }

    private suspend fun doOnEvent(event: EVENT) {
        BlocSupervisor.delegate?.onEvent(event)
        onEvent(event)
    }

    private suspend fun doOnTransition(transition: Transition<EVENT, STATE>) {
        BlocSupervisor.delegate?.onTransition(transition)
        onTransition(transition)
    }

    private suspend fun doOnError(error: Throwable) {
        BlocSupervisor.delegate?.onError(error)
        onError(error)
    }
}
