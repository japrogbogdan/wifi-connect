package io.connect.wifi.sdk

import android.content.Context
import java.lang.ref.SoftReference

/**
 * Entry point of SDK.
 *
 * @param activity - we required [android.app.Activity] because on android 30+ version
 * we should use its api to open other system activity. With simple [android.content.Context] we can't do it.
 *
 * @since 1.0.1
 */
class WifiConnectionCommander(private val activity: Context) {

    private var controller: SoftReference<Controller?>? = null

    /**
     * Start connection by passing your WifiRule instance
     *
     * @see io.connect.wifi.sdk.WifiRule
     */
    fun connectByRule(rule: WifiRule) {
        makeSureWeHaveController()
        controller?.get()?.startConnection(activity, rule)
    }

    private fun makeSureWeHaveController(){
        if (controller == null) {
            val reference = Controller()
            controller = SoftReference(reference)
        }
    }

    fun withStatusCallback(status: (ConnectStatus) -> Unit) {
        makeSureWeHaveController()
        controller?.get()?.addStatusCallback(status)
    }

    fun closeConnection() {
        controller?.get()?.resetConnection()
        controller = null
    }
}
