package io.connect.wifi.sdk.connect

import android.content.Intent
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.config.WifiConfig

/**
 * @suppress Internal api
 *
 * Delegate object that will find [io.connect.wifi.sdk.connect.delegate.ConnectionDelegate]
 * for specific [io.connect.wifi.sdk.config.WifiConfig] and start connection
 *
 * @see io.connect.wifi.sdk.connect.delegate.ConnectionDelegate
 *
 * @since 1.0.1
 */
internal class ConnectionManager(
    /**
     * @param - reference to [android.net.wifi.WifiManager]
     */
    wifiManager: WifiManager,

    /**
     * we use to run [android.app.Activity.startActivityForResult] on android 30+ versions
     */
    startActivityForResult: (Intent, Int) -> Unit
) {

    private val delegateFactory: DelegateFactory by lazy {
        DelegateFactory(wifiManager, startActivityForResult)
    }

    /**
     * Start connection for WifiConfig
     *
     * @see io.connect.wifi.sdk.config.WifiConfig
     */
    fun beginConnection(config: WifiConfig) {
        delegateFactory.provideDelegate(config)?.run {
            prepareDelegate()
            connect()
        }
    }
}