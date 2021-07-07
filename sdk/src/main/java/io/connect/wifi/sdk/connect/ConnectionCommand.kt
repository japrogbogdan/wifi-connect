package io.connect.wifi.sdk.connect

import io.connect.wifi.sdk.WifiConfigFactory
import io.connect.wifi.sdk.WifiRule
import java.util.concurrent.Callable

/**
 * @suppress Internal api
 *
 * Implementation of Runnable to avoid creating new anonymous class on every call
 *
 * @since 1.0.1
 */
internal class ConnectionCommand(
    /**
     * Config factory to get internal config for rule
     */
    private val factory: WifiConfigFactory,

    /**
     * Reference for delegate object that will use internal config to connect
     */
    private var manager: ConnectionManager?
) {

    /**
     * Rule reference
     */
    var wifiRule: WifiRule? = null

    fun execute() {
        factory.createConfig(wifiRule)?.let { config ->
            manager?.beginConnection(config)
        }
    }
}