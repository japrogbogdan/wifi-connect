package io.connect.wifi.sdk.task

import android.os.Handler
import io.connect.wifi.sdk.LogUtils
import io.connect.wifi.sdk.analytics.ConnectResult
import io.connect.wifi.sdk.analytics.ConnectionResultAnalyticsCommand
import io.connect.wifi.sdk.data.DeviceData
import io.connect.wifi.sdk.data.SessionData
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

internal class SendAnalyticsTask(
    mainThreadHandler: Handler,
    backgroundExecutor: ExecutorService,
    sessionData: SessionData,
    dump: DeviceData,
    connectionResult: LinkedList<ConnectResult>
) : BaseRetryTask<Unit>(
    mainThreadHandler = mainThreadHandler,
    backgroundExecutor = backgroundExecutor,
    func = {
        LogUtils.debug("[SendAnalyticsTask] send connection result to internal analytics")
        val analytics =
            ConnectionResultAnalyticsCommand(sessionData, dump, connectionResult)
        analytics.send()
    }, success = {
        LogUtils.debug("[SendAnalyticsTask] Delivered connection analytics info\n$connectionResult")
        connectionResult.clear()
    }, canRetry = {
        connectionResult.isNotEmpty()
    }
) {

    private val retryCount = AtomicInteger(0)

    override fun name() = "SendAnalyticsTask"

    override fun maxRepeatCount() = 5

    override fun getAndIncrement() = retryCount.getAndIncrement()

    override fun retryDelayMillis() = 60 * 1000L

}