package com.mews.app.bloc.example

sealed class MainEvent {
    object Increment : MainEvent()
    object Decrement : MainEvent()
}
