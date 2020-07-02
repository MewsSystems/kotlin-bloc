package com.mews.app.bloc.example

/**
 * State of the screen. In this simple case we only need one property: the value itself.
 * In a general case it's a good idea to implement your state as a sealed class with cases
 * named as nouns or adjectives, e.g. `Initial`, `Loading` or `Summary`.
 */
data class MainState(val value: Int = 0)
