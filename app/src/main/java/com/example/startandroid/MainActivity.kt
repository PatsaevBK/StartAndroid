package com.example.startandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val scope = CoroutineScope(Job() + CoroutineName("MyScope"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonRun).setOnClickListener {
            onRun()
        }

        findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            onCancel()
        }
    }

    private fun onCancel() {
        TODO("Not yet implemented")
    }

    private fun onRun() {
        log("onRun, start")

        scope.launch {
            log("coroutine, start")
            TimeUnit.MICROSECONDS.sleep(1000)
            log("coroutine, end")
        }

        log("onRun, middle")

        scope.launch {
            log("coroutine2, start")
            TimeUnit.MICROSECONDS.sleep(1500)
            log("coroutine2, end")
        }

        log("onRun, end")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        scope.cancel()
    }

    private fun log(text: String) {
        Log.d("TAG", "${formatter.format(Date())} $text [${Thread.currentThread().name}]")
    }
}