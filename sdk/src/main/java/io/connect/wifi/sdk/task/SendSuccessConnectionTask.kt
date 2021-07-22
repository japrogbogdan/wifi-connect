package io.connect.wifi.sdk.task

import android.os.Handler
import io.connect.wifi.sdk.LogUtils
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.network.SendSuccessConnectCallbackCommand
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

internal class SendSuccessConnectionTask(
    mainThreadHandler: Handler,
    backgroundExecutor: ExecutorService,
    sessionData: SessionData,
    url: String,
) : BaseRetryTask<Unit>(
    mainThreadHandler = mainThreadHandler,
    backgroundExecutor = backgroundExecutor,
    func = {
        LogUtils.debug("[SendSuccessConnectionTask] sending : $url")
        val cmd = SendSuccessConnectCallbackCommand(sessionData, url)
        cmd.sendRequest(null)
    }, success = {
        LogUtils.debug("[SendSuccessConnectionTask] Delivered url\n$url")
    }, canRetry = { true }
) {

    private val retryCount = AtomicInteger(1)

    override fun name() = "SendSuccessConnectionTask"

    override fun maxRepeatCount() = 3

    override fun getAndIncrement() = retryCount.getAndIncrement()

    override fun retryDelayMillis() = 5 * 1000L

}