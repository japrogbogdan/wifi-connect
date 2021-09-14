package io.connect.wifi.sdk.connect

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.cerificate.CertificateFactory
import io.connect.wifi.sdk.config.WifiConfig
import io.connect.wifi.sdk.connect.delegate.*
import io.connect.wifi.sdk.connect.delegate.SuggestionDelegate
import io.connect.wifi.sdk.connect.delegate.WebDelegate
import io.connect.wifi.sdk.connect.delegate.Wpa2Delegate
import io.connect.wifi.sdk.connect.delegate.Wpa2EapDelegate

/**
 * @suppress Internal api
 *
 * Factory that will provide [io.connect.wifi.sdk.connect.delegate.ConnectionDelegate]
 * implementation for specific [io.connect.wifi.sdk.config.WifiConfig]
 *
 * @since 1.0.1
 */
internal class DelegateFactory(
    /**
     * @param - reference to [android.net.wifi.WifiManager]
     */
    private val wifiManager: WifiManager,

    /**
     * we use to run [android.app.Activity.startActivityForResult] on android 30+ versions
     */
    private val startActivityForResult: (Intent, Int) -> Unit,

    private val certificateFactory: CertificateFactory,

    private val status: (ConnectStatus) -> Unit
) {

    /**
     * We use cache to avoid creating duplicate instances for the same config
     */
    private val cache = HashMap<WifiConfig, ConnectionDelegate>()

    /**
     *
     * Provide [io.connect.wifi.sdk.connect.delegate.ConnectionDelegate]
     * by [io.connect.wifi.sdk.config.WifiConfig] or null
     *
     * @param - [io.connect.wifi.sdk.config.WifiConfig]
     * @return - implementation of ConnectionDelegate or null
     *
     * @see io.connect.wifi.sdk.connect.delegate.ConnectionDelegate
     */
    fun provideDelegate(config: WifiConfig): ConnectionDelegate? {

        val cached: ConnectionDelegate? = cache[config]
        if (cached != null) return cached

        return when (config) {
            is WifiConfig.SupportNetworkWep -> WebDelegate(wifiManager, config).also {
                cache[config] = it
            }
            is WifiConfig.SupportNetworkWpa2 -> Wpa2Delegate(wifiManager, config, status).also {
                cache[config] = it
            }
            is WifiConfig.SupportNetworkWpa2Eap -> Wpa2EapDelegate(wifiManager, config).also {
                cache[config] = it
            }
            is WifiConfig.Wpa2PassphraseSuggestion -> {
                if (Build.VERSION.SDK_INT >= 29)
                    SuggestionDelegate(
                        wifiManager,
                        config,
                        status
                    ).also {
                        cache[config] = it
                    }
                else null
            }
            is WifiConfig.Wpa2Api30 -> {
                if (Build.VERSION.SDK_INT >= 30)
                    Wpa2Api30Delegate(
                        wifiManager,
                        config,
                        status
                    ).also {
                        cache[config] = it
                    }
                else null
            }
            is WifiConfig.SuggestionNetworkList -> {
                if (Build.VERSION.SDK_INT >= 29)
                    SuggestionListDelegate(
                        config,
                        startActivityForResult
                    ).also {
                        cache[config] = it
                    }
                else null
            }
            is WifiConfig.PasspointAddOrUpdateConfiguration -> {
                if (Build.VERSION.SDK_INT >= 26)
                    PasspointAddOrUpdateDelegate(
                        wifiManager,
                        config,
                        certificateFactory,
                        status
                    ).also {
                        cache[config] = it
                    }
                else null
            }
            is WifiConfig.PasspointResultConfiguration -> {
                if (Build.VERSION.SDK_INT >= 30)
                    PasspointResultDelegate(
                        config,
                        certificateFactory,
                        status,
                        startActivityForResult
                    ).also {
                        cache[config] = it
                    }
                else null
            }
            is WifiConfig.EnterpriseSuggestionConfiguration -> {
                if (Build.VERSION.SDK_INT >= 29)
                    EnterpriseSuggestionDelegate(
                        wifiManager,
                        config,
                        certificateFactory,
                        status
                    ).also {
                        cache[config] = it
                    }
                else null
            }
            else -> null
        }
    }
}