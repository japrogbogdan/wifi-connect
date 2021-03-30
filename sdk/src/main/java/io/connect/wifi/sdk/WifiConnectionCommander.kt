package io.connect.wifi.sdk

import android.app.Activity

/**
 * Entry point of SDK.
 *
 * @param activity - we required {@link android.app.Activity} because on android 30+ version
 * we should use its api to open other system activity. With simple {@link android.content.Context} we can't do it.
 *
 * @since 1.0.1
 */
class WifiConnectionCommander(private val activity: Activity) {

    /**
     * Start connection by passing your {@link io.connect.wifi.sdk.WifiRule} instance
     *
     * @see io.connect.wifi.sdk.WifiRule
     */
    fun connectByRule(rule: WifiRule) {
        Controller.startConnection(activity, rule)
    }
}
