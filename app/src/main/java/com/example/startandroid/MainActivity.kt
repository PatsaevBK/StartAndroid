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

    private lateinit var job: Job
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
        log("onCancel")
        job.cancel()
    }

    private fun onRun() {
        log("onRun, start")

        job = scope.launch {
            log("coroutine, start")
            var x = 0
            while (x < 5) {
                delay(1000)
                log("coroutine, ${x++}, isActive: $isActive")
            }
            log("coroutine, end")
        }

        log("onRun, end")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
        scope.cancel()
    }

    private fun log(text: String) {
        Log.d("TAG", "${formatter.format(Date())} $text [${Thread.currentThread().name}]")
    }
}