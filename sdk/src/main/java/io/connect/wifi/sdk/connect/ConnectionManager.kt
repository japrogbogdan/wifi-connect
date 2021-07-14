package io.connect.wifi.sdk.connect

import android.content.Intent
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.LogUtils
import io.connect.wifi.sdk.cerificate.CertificateFactory
import io.connect.wifi.sdk.config.WifiConfig
import java.lang.Exception

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
    startActivityForResult: (Intent, Int) -> Unit,

    certificateFactory: CertificateFactory,

    private val status: (ConnectStatus) -> Unit
) {

    private val delegateFactory: DelegateFactory by lazy {
        DelegateFactory(wifiManager, startActivityForResult, certificateFactory, status)
    }

    /**
     * Start connection for WifiConfig
     *
     * @see io.connect.wifi.sdk.config.WifiConfig
     */
    fun beginConnection(config: WifiConfig) {
        LogUtils.debug("[ConnectionManager] create delegate for wifi config $config")
        delegateFactory.provideDelegate(config)?.run {
            try {
                prepareDelegate()
                LogUtils.debug("[ConnectionManager] Try connect for delegate $this")
                connect()
            } catch (e: Throwable) {
                LogUtils.debug("[ConnectionManager] Failed connect for delegate $this", e)
                status.invoke(ConnectStatus.Error(Exception(e)))
            }
        } ?: kotlin.run {
            LogUtils.debug("[ConnectionManager] failed to create delegate for wifi config $config")
            status.invoke(ConnectStatus.Error(Exception("Can't use wifi config")))
        }
    }
}