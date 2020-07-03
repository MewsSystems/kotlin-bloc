package com.mews.app.bloc.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStateAtLeast
import com.mews.app.bloc.Bloc
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

fun <EVENT : Any, STATE : Any> LifecycleOwner.connect(
    bloc: Bloc<EVENT, STATE>,
    atLeast: Lifecycle.State = Lifecycle.State.RESUMED,
    onState: suspend (STATE) -> Unit
) {
    lifecycleScope.launch {
        lifecycle.whenStateAtLeast(atLeast) { bloc.onEach(onState).collect() }
    }
}
