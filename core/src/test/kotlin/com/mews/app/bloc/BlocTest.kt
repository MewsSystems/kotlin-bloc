package com.mews.app.bloc

import kotlinx.coroutines.flow.FlowCollector
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BlocTest {
    private val delegate = TestBlocDelegate()

    @Before
    fun setUp() {
        BlocSupervisor.delegate = delegate
    }

    @Test
    fun `collects events and transitions properly`() {
        runBlockingAndCancelScope {
            val bloc = object : BaseBloc<Int, String>(this) {
                override val initialState: String = "0"

                override suspend fun FlowCollector<String>.mapEventToState(event: Int) {
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
            Transition("1", 2, "2"),
            Transition("2", 3, "3")
        )
        Assert.assertEquals(expectedTransitions, delegate.transitions)

        Assert.assertTrue(delegate.errors.isEmpty())
    }

    @Test
    fun `collects errors properly`() {
        val error = IllegalArgumentException()
        runBlockingAndCancelScope {
            val bloc = object : BaseBloc<Int, String>(this) {
                override val initialState: String = "0"

                override suspend fun FlowCollector<String>.mapEventToState(event: Int) {
                    if (event == 2) throw error
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

        Assert.assertEquals(listOf(error), delegate.errors)
    }
}
