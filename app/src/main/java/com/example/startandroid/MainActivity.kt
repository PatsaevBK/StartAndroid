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

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        log("$throwable was handled in Coroutine_${context[CoroutineName]?.name}")
    }

    private val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)

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
        scope.launch(CoroutineName("1")) {
            launch(CoroutineName("1_1")) {
                val def = async(SupervisorJob(coroutineContext[Job])) {
                    TimeUnit.MILLISECONDS.sleep(500)
                    log("exception")
                    Integer.parseInt("one")
                }
                log("before await")
                //имеет смысл await обернуть в try catch только если хочешь чтобы остаток кода (repeatIsActive) выполнился
                try { def.await() } catch (e: Exception) { log("catch exception in try-catch") }
                repeatIsActive()
            }
            launch(CoroutineName("1_2")) {
                repeatIsActive()
            }
            repeatIsActive()
        }
        scope.launch(CoroutineName("2")) {
            launch(CoroutineName("2_1")) {
                repeatIsActive()
            }
            launch(CoroutineName("2_2")) {
                repeatIsActive()
            }
            repeatIsActive()
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
        log("scope, ${contextToString(scope.coroutineContext)}")
        scope.launch {
            log("coroutine, lvl 1, ${contextToString(this.coroutineContext)}")
            launch(Dispatchers.Default ) {
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
        "Job = ${context[Job]}, Dispatcher = ${context[ContinuationInterceptor]}}"

    private fun CoroutineScope.repeatIsActive() {
        repeat(5) {
            TimeUnit.MILLISECONDS.sleep(300)
            log("${coroutineContext[CoroutineName]?.name} is active: $isActive")
        }
    }
}