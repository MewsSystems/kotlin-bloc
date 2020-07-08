package com.mews.app.bloc

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class BlocTest {
    private val delegate = TestBlocDelegate()
    private val scope = TestCoroutineScope()

    @Before
    fun setUp() {
        BlocSupervisor.delegate = delegate
    }

    @Test
    fun `collects events and transitions properly`() {
        runBlocking {
            val bloc = object : BaseBloc<Int, String>(scope) {
                override val initialState: String = "0"

                override suspend fun Sink<String>.mapEventToState(event: Int) {
                    add(event.toString())
                }
            }
            bloc.add(1)
            bloc.add(2)
            bloc.add(3)
        }

        Assert.assertEquals(listOf(1, 2, 3), delegate.events)

        val expectedTransitions = listOf(
            Transition("0", 1, "1"),
            Transition("1", 2, "2"),
            Transition("2", 3, "3")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)

        Assert.assertTrue(delegate.errors.isEmpty())
    }

    @Test
    fun `collects errors properly`() {
        runBlocking {
            val bloc = object : BaseBloc<Int, String>(scope) {
                override val initialState: String = "0"

                override suspend fun Sink<String>.mapEventToState(event: Int) {
                    if (event == 2) throw IllegalArgumentException("Test error")
                    add(event.toString())
                }
            }
            bloc.add(1)
            bloc.add(2)
            bloc.add(3)
        }

        Assert.assertEquals(listOf(1, 2, 3), delegate.events)

        val expectedTransitions = listOf(
            Transition("0", 1, "1"),
            Transition("1", 3, "3")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)

        Assert.assertEquals(listOf("Test error"), delegate.errors.map { it.message })
    }

    @Test
    fun `can skip events in transformEvent`() {
        runBlocking {
            val bloc = object : BaseBloc<Int, String>(scope) {
                override val initialState: String = "0"

                override suspend fun Sink<String>.mapEventToState(event: Int) = add(event.toString())

                override fun transformEvents(flow: Flow<Int>): Flow<Int> =
                    flow.filter { it != 2 }
            }
            bloc.add(1)
            bloc.add(2)
            bloc.add(3)
        }

        Assert.assertEquals(listOf(1, 2, 3), delegate.events)

        val expectedTransitions = listOf(
            Transition("0", 1, "1"),
            Transition("1", 3, "3")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)
    }

    @Test
    fun `debounces event 2`() {
        runBlocking {
            val bloc = object : BaseBloc<Int, String>(scope) {
                override val initialState: String = "0"

                override suspend fun Sink<String>.mapEventToState(event: Int) = add(event.toString())

                // Could be simplified after https://github.com/Kotlin/kotlinx.coroutines/issues/2034
                override fun transformEvents(events: Flow<Int>): Flow<Int> = channelFlow {
                    val broadcast = events.broadcastIn(scope).asFlow()
                    val eventTwo = broadcast.filter { it == 2 }.debounce(100)
                    val otherEvents = broadcast.filter { it != 2 }
                    merge(eventTwo, otherEvents).collect { send(it) }
                }
            }
            bloc.add(1)
            bloc.add(2)
            bloc.add(2)
            bloc.add(2)
            bloc.add(3)
            scope.advanceTimeBy(1000)
        }

        val expectedTransitions = listOf(
            Transition("0", 1, "1"),
            Transition("1", 3, "3"),
            Transition("3", 2, "2")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)
    }

    @Test
    fun `can emit event during event processing`() {
        runBlocking {
            val bloc = object : BaseBloc<Int, String>(scope) {
                override val initialState: String = "0"

                override suspend fun Sink<String>.mapEventToState(event: Int) {
                    if (event == 2) add(10)
                    add(event.toString())
                }

                override suspend fun onEvent(event: Int) {
                    println("onEvent $event")
                }

                override suspend fun onError(error: Throwable) {
                    println(error)
                }
            }
            bloc.add(1)
            bloc.add(2)
            bloc.add(3)
        }

        val expectedTransitions = listOf(
            Transition("0", 1, "1"),
            Transition("1", 3, "3"),
            Transition("3", 2, "2")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)
    }
}
