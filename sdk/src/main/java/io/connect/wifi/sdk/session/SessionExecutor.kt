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
        WifiConnectionCommander(context).apply {
            withStatusCallback {
                when (it) {
                    ConnectStatus.Success -> {
                        queue.clear()
                        callback?.onStatusChanged(WiFiSessionStatus.Success)
                    }
                    is ConnectStatus.Error -> {
                        startIteration()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun start() {
        callback?.onStatusChanged(WiFiSessionStatus.RequestConfigs)
        currentFuture = backgroundExecutor.execute(
            func = {
                val request = RequestConfigCommand(sessionData)
                val data = request.sendRequest(sessionData.toJsonBody(dump))
                data?.toWifiRules() ?: emptyList()
            },
            resultHandler = mainThreadHandler,
            success = {
                queue.addAll(it)
            },
            error = {
                callback?.onStatusChanged(WiFiSessionStatus.Error(Exception(it)))
            },
            complete = {
                callback?.onStatusChanged(WiFiSessionStatus.Connecting)
                startIteration()
            }
        )
    }

    private fun startIteration() {
        queue.poll()?.let {
            commander.connectByRule(it)
        } ?: let {
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
    }
}