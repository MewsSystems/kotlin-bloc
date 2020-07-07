package com.mews.app.bloc.example

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mews.app.bloc.android.connect
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        plus.setOnClickListener { vm.addAsync(MainEvent.Incremented) }
        minus.setOnClickListener { vm.addAsync(MainEvent.Decremented) }

        connect(vm) { text.text = it.value.toString() }
    }
}
