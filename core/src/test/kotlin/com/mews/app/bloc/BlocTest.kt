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

                override suspend fun mapEventToState(event: Int): Flow<String> = flowOf(event.toString())
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

                override suspend fun mapEventToState(event: Int): Flow<String> = flow {
                    if (event == 2) throw IllegalArgumentException("Test error")
                    emit(event.toString())
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

                override suspend fun mapEventToState(event: Int): Flow<String> = flowOf(event.toString())

                override fun transformEvents(events: Flow<Int>): Flow<Int> = events.filter { it != 2 }
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

                override suspend fun mapEventToState(event: Int): Flow<String> = flowOf(event.toString())

                override fun transformEvents(events: Flow<Int>): Flow<Int> {
                    val eventTwo = events.filter { it == 2 }.debounce(100)
                    val otherEvents = events.filter { it != 2 }
                    return merge(eventTwo, otherEvents)
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
    fun `can emit event during mapEventToState`() {
        runBlocking {
            val bloc = object : BaseBloc<Int, String>(scope) {
                override val initialState: String = "0"

                override suspend fun mapEventToState(event: Int): Flow<String> = flow {
                    if (event == 2) {
                        add(10)
                    } else {
                        emit(event.toString())
                    }
                }
            }
            bloc.add(1)
            bloc.add(2)
            bloc.add(3)
        }

        val expectedTransitions = listOf(
            Transition("0", 1, "1"),
            Transition("1", 10, "10"),
            Transition("10", 3, "3")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)
    }
}
