package io.connect.wifi.sdk.task

import android.os.Handler
import com.google.gson.JsonSyntaxException
import io.connect.wifi.sdk.internal.LogUtils
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.network.SendSuccessConnectCallbackCommand
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

internal class SendSuccessConnectionTask(
    mainThreadHandler: Handler,
    backgroundExecutor: ExecutorService,
    sessionData: SessionData,
    url: String,
    connectionByLinkSend: (link: String) -> Unit,//successCallbackUrl
    connectionByLinkSuccess: (String?) -> Unit,
    connectionByLinkError: (Throwable?, String?) -> Unit
) : BaseRetryTask<String?>(//Unit
    mainThreadHandler = mainThreadHandler,
    backgroundExecutor = backgroundExecutor,
    func = {
        LogUtils.debug("[SendSuccessConnectionTask] sending : $url")
        connectionByLinkSend.invoke(url)
        val cmd = SendSuccessConnectCallbackCommand(sessionData, url)
        cmd.sendRequest(null)
    }, success = {
        LogUtils.debug("[SendSuccessConnectionTask] Delivered url\n$url")
        connectionByLinkSuccess.invoke(it)
    }, canRetry = { true },
    retryCondition = { response ->
        response?.let {
            try {
                val result = JSONObject(it).get("result") == "error"
                if (result)
                    connectionByLinkError.invoke(null, it)
                result
            } catch (er: JsonSyntaxException) {
                false
            }
        } ?: run { false }
    },
    error = { connectionByLinkError.invoke(it, null) }
) {

    private val retryCount = AtomicInteger(1)

    override fun name() = "SendSuccessConnectionTask"

    override fun maxRepeatCount() = 12

    override fun getAndIncrement() = retryCount.getAndIncrement()

    override fun retryDelayMillis() = 5 * 1000L

}