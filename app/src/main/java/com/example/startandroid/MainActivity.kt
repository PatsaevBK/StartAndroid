package com.example.startandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.*

class MainActivity : AppCompatActivity() {

    private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val scope = CoroutineScope(Dispatchers.Unconfined)

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
        findViewById<Button>(R.id.buttonRun2).setOnClickListener {
            onRun2()
        }
    }

    private fun onCancel() {
        log("onCancel")
        job.cancel()
    }

    private fun onRun() {
        val job1 = scope.launch {
            log("parent start")
            val res = async {
                log("child start")
                delay(1000)
                log("child end")
            }
            res.await()
            log("parent end")
        }
        scope.launch {
            delay(500)
            log("parent job is active: ${job1.isActive}")
            delay(1000)
            log("parent job is active: ${job1.isActive}")
        }
    }

    private suspend fun getData(): String = suspendCoroutine {
        log("suspend function, start")
        thread {
            log("suspend function, background work")
            TimeUnit.MILLISECONDS.sleep(1000)
            it.resume("Data")
        }
    }
    private suspend fun getData2(): String {
        return ""
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

    private fun contextToString(context: CoroutineContext) =
        "Job = ${context[Job]}, Dispatcher = ${context[ContinuationInterceptor]}}"
}