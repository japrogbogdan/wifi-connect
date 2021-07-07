package io.connect.wifi.sdk.util

import android.os.Handler
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

internal fun <R> Executor.execute(
    func: Callable<R>,
    resultHandler: Handler,
    success: (R) -> Unit,
    error: (Throwable) -> Unit,
    complete: () -> Unit
): Future<R> {
    val future = object : FutureTask<R>(func) {
        val callback = Runnable {
            if (!isCancelled) {
                var ok = false
                try {
                    val result = get()
                    ok = true
                    success(result)
                } catch (e: java.util.concurrent.ExecutionException) {
                    if (!ok) {
                        error(e)
                    }
                }
                complete()
            }
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            return super.cancel(mayInterruptIfRunning)
                .also {
                    resultHandler.removeCallbacks(callback)
                }
        }

        override fun done() {
            if (!isCancelled) {
                resultHandler.post(callback)
            }
        }
    }
    execute(future)
    return future
}