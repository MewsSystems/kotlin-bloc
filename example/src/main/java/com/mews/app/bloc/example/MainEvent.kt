package com.mews.app.bloc.example

/**
 * Events that can be passed to BLoC. It usually means that something happened,
 * so it normally should be a verb in a past tense.
 */
sealed class MainEvent {
    object Incremented : MainEvent()
    object Decremented : MainEvent()
}
