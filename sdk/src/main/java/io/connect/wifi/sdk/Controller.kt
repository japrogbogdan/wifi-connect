package io.connect.wifi.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.cerificate.factory.CertificateFactoryImpl
import io.connect.wifi.sdk.cerificate.storage.CertificateStorageImpl
import io.connect.wifi.sdk.connect.ConnectionCommand
import io.connect.wifi.sdk.connect.ConnectionManager
import java.lang.ref.SoftReference
import java.util.concurrent.Executors
import java.util.concurrent.Future
import io.connect.wifi.sdk.util.execute

/**
 * @suppress Internal api
 *
 * controller that will communicate with other SDK parts. This is an entry point of internal
 * sdk layer.
 *
 * @since 1.0.1
 */
internal class Controller {

    private val mainThreadHandler: Handler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }
    private val backgroundExecutor by lazy {
        Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        ).also {
            it.execute { Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND) }
        }
    }

    private val factory: WifiConfigFactory by lazy { WifiConfigFactory() }
    private var manager: ConnectionManager? = null
    private var command: ConnectionCommand? = null
    private var activityReference: SoftReference<Activity>? = null
    private var certificateFactory: CertificateFactoryImpl? = null
    private var currentFuture: Future<*>? = null
    private var statusCallback : ((ConnectStatus) -> Unit)? = null

    /**
     * Start connection by rules.
     * We'll proceed this request in background thread. However, we'r able to use main thread to
     * access [android.app.Activity] functions.
     *
     * @param context - provide [android.content.Context] to grant access to [android.net.wifi.WifiManager]
     * @param rule - provide WifiRule to define how to connect to wifi
     *
     * @see io.connect.wifi.sdk.WifiRule
     */
    fun startConnection(context: Context, rule: WifiRule) {
        initParams(context)
        doConnect(rule)
    }

    fun addStatusCallback(callback: (ConnectStatus) -> Unit) {
        statusCallback = callback
    }

    private val startActivityForResult: (Intent, Int) -> Unit = { intent, i ->
        mainThreadHandler.post {
            activityReference?.get()?.startActivityForResult(intent, i)
        }
    }

    private val connectStatus: (ConnectStatus) -> Unit = { status ->
        statusCallback?.invoke(status)
    }

    /**
     * Instantiate all references to be used later.
     */
    private fun initParams(context: Context) {
        if (manager == null) {
            val wifi =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            (context as? Activity)?.let {
                activityReference = SoftReference(it)
            }
            certificateFactory = CertificateFactoryImpl(CertificateStorageImpl())
            manager =
                ConnectionManager(wifi, startActivityForResult, certificateFactory!!, connectStatus)
        }

        if (command == null) {
            command = ConnectionCommand(factory, manager, connectStatus)
        }
    }


    private fun doConnect(rule: WifiRule) {
        cancelConnect()
        currentFuture = backgroundExecutor.execute(
            func = {
                command?.let {
                    it.wifiRule = rule
                    it.execute()
                }
            },
            resultHandler = mainThreadHandler,
            success = {},
            error = {},
            complete = {}
        )
    }

    fun resetConnection() {
        cancelConnect()
        activityReference?.clear()
        activityReference = null
        certificateFactory = null
        manager = null
        command = null
        statusCallback = null
    }

    private fun cancelConnect() {
        currentFuture?.let {
            it.cancel(false)
            currentFuture = null
        }
    }
}