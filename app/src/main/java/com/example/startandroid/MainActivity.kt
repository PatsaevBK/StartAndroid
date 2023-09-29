package com.example.startandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val scope = CoroutineScope(Dispatchers.Main)

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
            val userData = coroutineContext[UserData]
            Log.d("TAG", userData.toString())
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
        log("scope, ${contextToString(scope.coroutineContext)}")
        scope.launch {
            log("coroutine, lvl 1, ${contextToString(this.coroutineContext)}")
            launch(Dispatchers.Default + UserData(1,"Kek", 18)) {
                log("coroutine, lvl 2, ${contextToString(coroutineContext)}")
                launch {
                    log("coroutine, lvl 3 ${contextToString(coroutineContext)}")
                }
            }
        }
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
        "Job = ${context[Job]}, Dispatcher = ${context[ContinuationInterceptor]}, User = ${context[UserData]}"
}