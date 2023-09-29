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

    private val scope = CoroutineScope(Dispatchers.Default)

    private lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log("context = ${scope.coroutineContext}")
        findViewById<Button>(R.id.buttonRun).setOnClickListener {
            onRun()
        }

        findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            onCancel()
        }
        findViewById<Button>(R.id.buttonRun2).setOnClickListener {
            onRun2()
        }
    }

    private fun onCancel() {
        log("onCancel")
        job.cancel()
    }

    private fun onRun() {
        scope.launch {
            log("parent coroutine, start")

            val data = async { getData() }
            val data2 = async { getData2() }
            val result = "${data.await()}, ${data2.await()}"


            log("parent coroutine, end. Result = $result")
        }
    }

    private suspend fun getData(): String {
        log("getData")
        delay(1000)
        return "data"
    }

    private suspend fun getData2(): String {
        log("getData2")
        delay(1000)
        return "data2"
    }

    private fun onRun2() {
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