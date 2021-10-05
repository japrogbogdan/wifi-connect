package io.connect.wifi.sdk.session

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.*
import io.connect.wifi.sdk.activity.ActivityHelper
import io.connect.wifi.sdk.analytics.ConnectResult
import io.connect.wifi.sdk.data.DeviceData
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.internal.LogUtils
import io.connect.wifi.sdk.network.RequestConfigCommand
import io.connect.wifi.sdk.task.SendAnalyticsTask
import io.connect.wifi.sdk.task.SendSuccessConnectionTask
import io.connect.wifi.sdk.util.LocalCache
import io.connect.wifi.sdk.util.execute
import io.connect.wifi.sdk.util.toWifiRules
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList

/**
 * @suppress Internal api
 *
 * Controller to handle run/stop current session
 */
internal class SessionExecutor(
    private val context: Context,
    private val sessionData: SessionData,
    private val dump: DeviceData,
    private val callback: WifiSessionCallback?,
    private val activityHelper: ActivityHelper?,
    private val autoDeliverSuccessCallback: Boolean
) {

    private val mainThreadHandler: Handler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }
    private val backgroundExecutor by lazy {
        Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        ).also {
            it.execute { Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND) }
        }
    }

    private var currentFuture: Future<*>? = null
    private val cache: LocalCache by lazy { LocalCache(context) }

    /**
     * Temporary cache for rules that we may use for connecting to wifi.
     * We'll remove item before each connection attempts. Empty list mean that we'd tried all
     * rules & there's no more or we didn't fetch any rule from api.
     */
    private val queue = LinkedList<WifiRule>()

    /**
     * Commander to do connection request by rules
     */
    private val commander: WifiConnectionCommander by lazy {
        WifiConnectionCommander(
            context,
            activityHelper
        )
    }

    /**
     * Analytics cache for connection attempt
     */
    private val connectionResult = LinkedList<ConnectResult>()

    /**
     * Retry analytics task
     */
    private var retryAnalytics: SendAnalyticsTask? = null

    private var successCallback: SendSuccessConnectionTask? = null

    /**
     * Сonfiguration request
     */
    fun requestConfigs() {
        if (commander.isWifiEnabled().not()) {
            notifyStatusChanged(WiFiSessionStatus.DisabledWifi)
            return
        }
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
                (it.first as? ArrayList<WifiRule>)?.let {
                    cache.listWiFiRule = it
                }
                cache.traceId = it.second
            },
            error = {
                LogUtils.debug("[SessionExecutor] Request configs error", it)
                notifyStatusChanged(WiFiSessionStatus.Error(Exception(it)))
            },
            complete = {
                LogUtils.debug("[SessionExecutor] Request remote finished")
                notifyStatusChanged(WiFiSessionStatus.ReceivedConfigs)
            }
        )
    }

    /**
     * Try connect with rule
     */
    fun startConnection() {
        if (commander.isWifiEnabled().not()) {
            notifyStatusChanged(WiFiSessionStatus.DisabledWifi)
            return
        }
        notifyStatusChanged(WiFiSessionStatus.Connecting)

        //Get config from cache
        cache.listWiFiRule?.let { cacheListRule ->
            queue.addAll(cacheListRule)
            sessionData.traceId = cache.traceId.orEmpty()
        }
        startIteration()
    }

    /**
     * Try connect with rule
     */
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
                        rule.successCallbackUrl?.let { i ->
                            if (i.isNotEmpty()) triggerSuccessCallbackUrl(i)
                        } ?: run {
                            notifyStatusChanged(
                                WiFiSessionStatus.ConnectionByLinkSend(
                                    "NULL"
                                )
                            )
                        }
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

    /**
     * Drop session
     */
    fun cancel() {
        currentFuture?.let {
            it.cancel(false)
            currentFuture = null
        }
        commander.closeConnection()
        connectionResult.clear()
        cleanup()
        notifyStatusChanged(WiFiSessionStatus.CancelSession)
        LogUtils.debug("[SessionExecutor] Canceled session")
    }

    private fun cleanup() {
        retryAnalytics?.let { mainThreadHandler.removeCallbacks(it) }
        successCallback?.let { mainThreadHandler.removeCallbacks(it) }
        retryAnalytics = null
        successCallback = null
    }

    private fun sendConnectionResultToAnalytics() {
        retryAnalytics = SendAnalyticsTask(
            mainThreadHandler,
            backgroundExecutor,
            sessionData,
            dump,
            connectionResult
        )
        mainThreadHandler.post(retryAnalytics!!)
    }

    private fun triggerSuccessCallbackUrl(url: String) {
        if (autoDeliverSuccessCallback) {
            successCallback =
                SendSuccessConnectionTask(mainThreadHandler, backgroundExecutor, sessionData, url,
                    connectionByLinkSend = { link ->
                        notifyStatusChanged(
                            WiFiSessionStatus.ConnectionByLinkSend(
                                link
                            )
                        )
                    },
                    connectionByLinkSuccess = {
                        notifyStatusChanged(WiFiSessionStatus.ConnectionByLinkSuccess(it))
                    },
                    connectionByLinkError = { throwable, string ->
                        throwable?.let { notifyStatusChanged(WiFiSessionStatus.Error(Exception(it))) }
                        string?.let { notifyStatusChanged(WiFiSessionStatus.Error(Exception(it))) }
                    })
            mainThreadHandler.post(successCallback!!)
        }
    }

    private fun notifyStatusChanged(status: WiFiSessionStatus) {
        mainThreadHandler.post { callback?.onStatusChanged(status) }
    }
}