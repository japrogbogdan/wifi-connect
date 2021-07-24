package io.connect.wifi.sdk.task

import android.os.Handler
import io.connect.wifi.sdk.internal.LogUtils
import io.connect.wifi.sdk.util.execute
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

internal abstract class BaseRetryTask<R>(
    private val mainThreadHandler: Handler,
    private val backgroundExecutor: ExecutorService,
    private val func: Callable<R>,
    private val success: (R) -> Unit,
    private val canRetry: () -> Boolean
) : RetryTask {

    private var currentFuture: Future<*>? = null

    override fun run() {
        sendCommand()
    }

    private fun sendCommand() {
        currentFuture = backgroundExecutor.execute(
            func = func,
            resultHandler = mainThreadHandler,
            success = success.also {
                clean()
            },
            error = { retryCommand() },
            complete = {}
        )
    }

    private fun retryCommand() {
        if (canRetry() && maxRepeatCount() > getAndIncrement()) {
            LogUtils.debug("[${name()}] retry deliver task in ${retryDelayMillis()} millis")
            mainThreadHandler.postDelayed(this, retryDelayMillis())
        } else clean()
    }

    private fun clean() {
        currentFuture?.let {
            it.cancel(false)
            currentFuture = null
        }
        mainThreadHandler.removeCallbacks(this)
    }
}