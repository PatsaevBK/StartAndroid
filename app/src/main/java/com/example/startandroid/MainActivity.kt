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

    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        log("exception was handled in Coroutine_${coroutineContext[CoroutineName]?.name}")
    }
    private val scope = CoroutineScope( handler)


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

    @OptIn(ExperimentalStdlibApi::class)
    private fun onRun() {
        log("start onRun")
        scope.launch( CoroutineName("1")  ) {
            log("start coroutine 1 ${coroutineContext[CoroutineDispatcher.Key].toString()}")
            launch(CoroutineName("1_1")) {
                TimeUnit.MILLISECONDS.sleep(500)
                val n = Integer.parseInt("one")
            }
            launch(CoroutineName("1_2")) {
                TimeUnit.MILLISECONDS.sleep(1000)
            }
        }

        scope.launch(CoroutineName("2")) {
            launch(CoroutineName("2_1")) {
                TimeUnit.MILLISECONDS.sleep(2000)
                log("${coroutineContext[CoroutineName]?.name}")
            }
            launch(CoroutineName("2_2")) {
                TimeUnit.MILLISECONDS.sleep(3000)
            }
        }

        log("end onRun")
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