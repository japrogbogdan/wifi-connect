package io.connect.wifi.sdk.connect

import io.connect.wifi.sdk.WifiConfigFactory
import io.connect.wifi.sdk.WifiRule

internal class ConnectionCommand(
    private val factory: WifiConfigFactory,
    private var manager: ConnectionManager?
) : Runnable {

    var wifiRule: WifiRule? = null

    override fun run() {
        factory.createConfig(wifiRule)?.let { config ->
            manager?.beginConnection(config)
        }
    }
}