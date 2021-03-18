package io.connect.wifi.sdk.connect

import android.content.Intent
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.config.WifiConfig

internal class ConnectionManager(wifiManager: WifiManager, startActivityForResult: (Intent, Int) -> Unit) {

    private val delegateFactory: DelegateFactory by lazy {
        DelegateFactory(wifiManager, startActivityForResult)
    }

    fun beginConnection(config: WifiConfig){
        delegateFactory.provideDelegate(config)?.run {
            prepareDelegate()
            connect()
        }
    }
}