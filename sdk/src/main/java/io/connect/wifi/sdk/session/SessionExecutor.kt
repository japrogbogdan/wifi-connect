package io.connect.wifi.sdk.session

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.*
import io.connect.wifi.sdk.data.DeviceData
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.network.RequestConfigCommand
import io.connect.wifi.sdk.util.execute
import io.connect.wifi.sdk.util.toWifiRules
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class SessionExecutor(
    private val context: Context,
    private val sessionData: SessionData,
    private val dump: DeviceData,
    private val callback: WifiSessionCallback?
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
    private val queue = LinkedList<WifiRule>()
    private val commander: WifiConnectionCommander by lazy {
        WifiConnectionCommander(context)
    }

    fun start() {
        LogUtils.debug("[SessionExecutor] Request remote configs")
        callback?.onStatusChanged(WiFiSessionStatus.RequestConfigs)
        currentFuture = backgroundExecutor.execute(
            func = {
                val request = RequestConfigCommand(sessionData)
                val data = request.sendRequest(sessionData.toJsonBody(dump))
                data?.toWifiRules() ?: emptyList()
            },
            resultHandler = mainThreadHandler,
            success = {
                LogUtils.debug("[SessionExecutor] Received ${it.size} remote configs")
                queue.addAll(it)
            },
            error = {
                LogUtils.debug("[SessionExecutor] Request configs error", it)
                callback?.onStatusChanged(WiFiSessionStatus.Error(Exception(it)))
            },
            complete = {
                LogUtils.debug("[SessionExecutor] Request remote finished")
                callback?.onStatusChanged(WiFiSessionStatus.Connecting)
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
                        callback?.onStatusChanged(WiFiSessionStatus.Success)
                    }
                    is ConnectStatus.Error -> {
                        LogUtils.debug("[SessionExecutor] FAILED connect by rule $rule", it.reason)
                        startIteration()
                    }
                    else -> {
                    }
                }
            }
            commander.connectByRule(rule)
        } ?: let {
            LogUtils.debug("[SessionExecutor] We don't have rules in queue")
            callback?.onStatusChanged(WiFiSessionStatus.Error(Exception("Missing wifi configs")))
        }
    }

    fun cancel() {
        currentFuture?.let {
            it.cancel(false)
            currentFuture = null
        }
        commander.closeConnection()
        callback?.onStatusChanged(WiFiSessionStatus.CancelSession)
        LogUtils.debug("[SessionExecutor] Canceled session")
    }
}