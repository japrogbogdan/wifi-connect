package io.connect.wifi.sdk

import android.content.Context
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.activity.ActivityHelper
import io.connect.wifi.sdk.internal.Controller
import io.connect.wifi.sdk.internal.LogUtils
import java.lang.ref.SoftReference

/**
 * Entry point of SDK for manual connection to wifi.
 *
 * @param activity - we required [android.app.Activity] because on android 30+ version
 * we should use its api to open other system activity. With simple [android.content.Context] we can't do it.
 *
 * @since 1.0.1
 */
class WifiConnectionCommander(
    private val activity: Context,
    /**
     * Helper class to call activity.startActivityForResult when Context is not enough
     */
    private val activityHelper: ActivityHelper? = null
) {

    private var controller: SoftReference<Controller?>? = null

    /*
    * Check On WiFiModule
     */
    fun isWifiEnabled() =
        (activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled

    /**
     * Start connection by passing your WifiRule instance
     *
     * @see io.connect.wifi.sdk.WifiRule
     */
    fun connectByRule(rule: WifiRule) {
        makeSureWeHaveController()
        controller?.get()?.startConnection(activity, rule) ?: kotlin.run {
            LogUtils.debug("[WifiConnectionCommander] Can't start connection. Missing controller")
        }
    }

    private fun makeSureWeHaveController() {
        if (controller == null) {
            val reference = Controller(activityHelper)
            controller = SoftReference(reference)
        }
    }

    /**
     * Add callback for status changes
     */
    fun withStatusCallback(status: (ConnectStatus) -> Unit) {
        makeSureWeHaveController()
        controller?.get()?.addStatusCallback(status) ?: kotlin.run {
            LogUtils.debug("[WifiConnectionCommander] Can't add status callback. Missing controller")
        }
    }

    /**
     * Drop connection attempt
     */
    fun closeConnection() {
        activityHelper?.cleanup()
        controller?.get()?.resetConnection()
        controller = null
    }
}
