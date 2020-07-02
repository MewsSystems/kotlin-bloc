package com.mews.app.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseBloc<EVENT : Any, STATE : Any>(private val scope: CoroutineScope) :
    Bloc<EVENT, STATE> {
    protected abstract val initialState: STATE

    private val stateFlow by lazy { MutableStateFlow(initialState) }

    override val state: STATE get() = stateFlow.value

    protected abstract suspend fun FlowCollector<STATE>.mapEventToState(event: EVENT)

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<STATE>) = stateFlow.collect(collector)

    private val eventChannel = Channel<EVENT>()

    override suspend fun add(event: EVENT) {
        try {
            doOnEvent(event)
            eventChannel.send(event)
        } catch (e: Throwable) {
            doOnError(e)
        }
    }

    override fun addAsync(event: EVENT) {
        scope.launch { add(event) }
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

    protected open suspend fun onTransition(transition: Transition<EVENT, STATE>) {}

    protected open suspend fun onError(error: Throwable) {}

    protected open suspend fun onEvent(event: EVENT) {}

    init {
        eventChannel.consumeAsFlow()
            .flatMapConcat { event ->
                flow<STATE> { mapEventToState(event) }
                    .catch { doOnError(it) }
                    .map { Transition(stateFlow.value, event, it) }
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
}
