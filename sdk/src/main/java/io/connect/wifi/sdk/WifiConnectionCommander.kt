package io.connect.wifi.sdk

import android.app.Activity

class WifiConnectionCommander(private val activity: Activity) {

    fun connectByRule(rule: WifiRule){
        Controller.startConnection(activity, rule)
    }
}
