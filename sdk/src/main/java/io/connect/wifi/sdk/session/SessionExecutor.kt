package io.connect.wifi.sdk.session

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.*
import io.connect.wifi.sdk.analytics.ConnectResult
import io.connect.wifi.sdk.analytics.ConnectionResultAnalyticsCommand
import io.connect.wifi.sdk.data.DeviceData
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.network.RequestConfigCommand
import io.connect.wifi.sdk.util.execute
import io.connect.wifi.sdk.util.toWifiRules
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

internal class SessionExecutor(
    private val context: Context,
    private val sessionData: SessionData,
    private val dump: DeviceData,
    private val callback: WifiSessionCallback?
) {

    companion object {
        private const val MAX_RETRY_COUNT = 5
        private const val RETRY_DELAY_MILLIS = 60 * 1000L
    }

    private val mainThreadHandler: Handler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }
    private val backgroundExecutor by lazy {
        Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        ).also {
            it.execute { Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND) }
        }
    }

    private var currentFuture: Future<*>? = null
    private val queue = LinkedList<WifiRule>()
    private val commander: WifiConnectionCommander by lazy { WifiConnectionCommander(context) }
    private val connectionResult = LinkedList<ConnectResult>()
    private val retryCount = AtomicInteger(0)
    private val retryAnalytics = Runnable { sendConnectionResultToAnalytics() }


    fun start() {
        LogUtils.debug("[SessionExecutor] Request remote configs")
        notifyStatusChanged(WiFiSessionStatus.RequestConfigs)
        currentFuture = backgroundExecutor.execute(
            func = {
                val request = RequestConfigCommand(sessionData)
                val data = request.sendRequest(sessionData.toJsonBody(dump))
                data?.toWifiRules() ?: Pair(emptyList(), "")
            },
            resultHandler = mainThreadHandler,
            success = {
                LogUtils.debug("[SessionExecutor] Received ${it.first.size} remote configs")
                queue.addAll(it.first)
                sessionData.traceId = it.second
            },
            error = {
                LogUtils.debug("[SessionExecutor] Request configs error", it)
                notifyStatusChanged(WiFiSessionStatus.Error(Exception(it)))
            },
            complete = {
                LogUtils.debug("[SessionExecutor] Request remote finished")
                notifyStatusChanged(WiFiSessionStatus.Connecting)
                startIteration()
            }
        )
    }

    private fun startIteration() {
        queue.poll()?.let { rule ->
            LogUtils.debug("[SessionExecutor] Try connect by rule $rule")
            commander.withStatusCallback {
                when (it) {
                    ConnectStatus.Success -> {
                        LogUtils.debug("[SessionExecutor] SUCCESS connect by rule $rule")
                        queue.clear()
                        connectionResult.add(
                            ConnectResult(rule, io.connect.wifi.sdk.analytics.ConnectStatus.Success)
                        )
                        sendConnectionResultToAnalytics()
                        notifyStatusChanged(WiFiSessionStatus.Success)
                    }
                    is ConnectStatus.Error -> {
                        LogUtils.debug("[SessionExecutor] FAILED connect by rule $rule", it.reason)
                        connectionResult.add(
                            ConnectResult(
                                rule,
                                io.connect.wifi.sdk.analytics.ConnectStatus.Error,
                                it.reason.message
                            )
                        )
                        startIteration()
                    }
                    else -> {
                    }
                }
            }
            commander.connectByRule(rule)
        } ?: let {
            LogUtils.debug("[SessionExecutor] We don't have rules in queue")
            sendConnectionResultToAnalytics()
            notifyStatusChanged(WiFiSessionStatus.Error(Exception("Missing wifi configs")))
        }
    }

    fun cancel() {
        currentFuture?.let {
            it.cancel(false)
            currentFuture = null
        }
        commander.closeConnection()
        mainThreadHandler.removeCallbacks(retryAnalytics)
        connectionResult.clear()
        notifyStatusChanged(WiFiSessionStatus.CancelSession)
        LogUtils.debug("[SessionExecutor] Canceled session")
    }

    private fun sendConnectionResultToAnalytics() {
        currentFuture = backgroundExecutor.execute(
            func = {
                LogUtils.debug("[SessionExecutor] send connection result to internal analytics")
                val analytics =
                    ConnectionResultAnalyticsCommand(sessionData, dump, connectionResult)
                analytics.send()
            },
            resultHandler = mainThreadHandler,
            success = {
                LogUtils.debug("[SessionExecutor] Delivered connection analytics info\n$connectionResult")
                connectionResult.clear()
            },
            error = {
                LogUtils.debug(
                    "[SessionExecutor] Failed to send connection analytics info\n$connectionResult",
                    it
                )
                retryDeliverAnalytics()
            },
            complete = {

            }
        )
    }

    private fun retryDeliverAnalytics() {
        if (connectionResult.isNotEmpty() && MAX_RETRY_COUNT > retryCount.getAndIncrement()) {
            LogUtils.debug("[SessionExecutor] retry deliver analytics in $RETRY_DELAY_MILLIS millis")
            mainThreadHandler.postDelayed(retryAnalytics, RETRY_DELAY_MILLIS)
        } else mainThreadHandler.removeCallbacks(retryAnalytics)
    }

    private fun notifyStatusChanged(status: WiFiSessionStatus) {
        mainThreadHandler.post { callback?.onStatusChanged(status) }
    }
}