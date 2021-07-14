package io.connect.wifi.sdk.connect

import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.LogUtils
import io.connect.wifi.sdk.WifiConfigFactory
import io.connect.wifi.sdk.WifiRule
import java.lang.Exception

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
    private var manager: ConnectionManager?,

    private val status: (ConnectStatus) -> Unit
) {

    /**
     * Rule reference
     */
    var wifiRule: WifiRule? = null

    fun execute() {
        LogUtils.debug("[ConnectionCommand] create wifi config")
        factory.createConfig(wifiRule)?.let { config ->
            manager?.beginConnection(config)
        } ?: kotlin.run {
            LogUtils.debug("[ConnectionCommand] failed to create wifi config")
            status.invoke(ConnectStatus.Error(Exception("Can't use current rule")))
        }
    }
}