package io.connect.wifi.sdk.internal

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.*
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.WifiRule
import io.connect.wifi.sdk.activity.ActivityHelper
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
internal class Controller(private val activityHelper: ActivityHelper?) {

    private val mainThreadHandler: Handler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }
    private val backgroundExecutor by lazy {
        Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        ).also {
            it.execute { Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND) }
        }
    }

    private val factory: WifiConfigFactory by lazy { WifiConfigFactory() }
    private var wifiManager: WifiManager? = null
    private var rule: WifiRule? = null
    private var manager: ConnectionManager? = null
    private var command: ConnectionCommand? = null
    private var activityReference: SoftReference<Activity>? = null
    private var certificateFactory: CertificateFactoryImpl? = null
    private var currentFuture: Future<*>? = null
    private var statusCallback: ((ConnectStatus) -> Unit)? = null
    private var ctx: Context? = null

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
        ctx = context
        initParams(context)
        when {
            Build.VERSION.SDK_INT <= 29 -> doScanWiFiAndConnection(rule)
            else -> doConnect(rule)
        }
    }

    fun addStatusCallback(callback: (ConnectStatus) -> Unit) {
        statusCallback = callback
    }

    private val startActivityForResult: (Intent, Int) -> Unit = { intent, i ->
        mainThreadHandler.post {
            activityReference?.get()?.startActivityForResult(intent, i)
                ?: activityHelper?.startActivityForResult(intent, i)
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
            LogUtils.debug("[Controller] init references")
            val wifi =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager = wifi
            val connectivityManager =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            (context as? Activity)?.let {
                activityReference = SoftReference(it)
            }

            certificateFactory = CertificateFactoryImpl(CertificateStorageImpl())
            manager =
                ConnectionManager(
                    wifi,
                    connectivityManager,
                    startActivityForResult,
                    certificateFactory!!,
                    connectStatus
                )
        }

        if (command == null) {
            command = ConnectionCommand(factory, manager, connectStatus)
        }
    }

    /**
     * Wifi Scan Receiver
     */
    val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            ctx?.unregisterReceiver(this)
            scanResults()
        }
    }

    private fun scanResults() {
        wifiManager?.scanResults?.let { scanResults ->
            rule?.let { wifiRule ->
                if (scanResults.count { it.SSID == wifiRule.ssid } > 0)
                    doConnect(wifiRule)
                else {
                    LogUtils.debug("Wifi scanSucce Can't find wifi current ssid")
                    connectStatus.invoke(ConnectStatus.NotFoundWiFiPoint(wifiRule.ssid))
                }
            }
        }
    }

    private fun doScanWiFiAndConnection(wifiRule: WifiRule) {
        rule = wifiRule
        // Register the receiver
        ctx?.registerReceiver(
            wifiScanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
        val success = wifiManager?.startScan() ?: false
        if (success.not()) {
            scanResults()
            ctx?.unregisterReceiver(wifiScanReceiver)
        }
    }

    private fun doConnect(rule: WifiRule) {
        cancelConnect()
        currentFuture = backgroundExecutor.execute(
            func = {
                LogUtils.debug("[Controller] try connect with rule $rule")
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
        ctx = null
    }
}