package io.connect.wifi.sdk.task

import android.os.Handler
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.connect.wifi.sdk.data.ConnectResponseData
import io.connect.wifi.sdk.internal.LogUtils
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.network.SendSuccessConnectCallbackCommand
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

internal class SendSuccessConnectionTask(
    mainThreadHandler: Handler,
    backgroundExecutor: ExecutorService,
    sessionData: SessionData,
    url: String
) : BaseRetryTask<String?>(//Unit
    mainThreadHandler = mainThreadHandler,
    backgroundExecutor = backgroundExecutor,
    func = {
        LogUtils.debug("[SendSuccessConnectionTask] sending : $url")
        val cmd = SendSuccessConnectCallbackCommand(sessionData, url)
        cmd.sendRequest(null)
    }, success = {
        LogUtils.debug("[SendSuccessConnectionTask] Delivered url\n$url")
    }, canRetry = { true },
    retryСondition = { response ->
        response?.let {
            try {
                Gson().fromJson(it, ConnectResponseData::class.java).result == "error"
            } catch (er: JsonSyntaxException) {
                false
            }
        } ?: run { false }
    }
) {

    private val retryCount = AtomicInteger(1)

    override fun name() = "SendSuccessConnectionTask"

    override fun maxRepeatCount() = 12

    override fun getAndIncrement() = retryCount.getAndIncrement()

    override fun retryDelayMillis() = 5 * 1000L

}