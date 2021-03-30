package io.connect.wifi.sdk.connect

import android.annotation.SuppressLint
import android.content.Intent
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.config.WifiConfig
import io.connect.wifi.sdk.connect.delegate.*
import io.connect.wifi.sdk.connect.delegate.SuggestionDelegate
import io.connect.wifi.sdk.connect.delegate.WebDelegate
import io.connect.wifi.sdk.connect.delegate.Wpa2Delegate
import io.connect.wifi.sdk.connect.delegate.Wpa2EapDelegate

/**
 * @suppress Internal api
 *
 * Factory that will provide {@link io.connect.wifi.sdk.connect.delegate.ConnectionDelegate}
 * implementation for specific {@link io.connect.wifi.sdk.config.WifiConfig}
 *
 * @since 1.0.1
 */
internal class DelegateFactory(
    /**
     * @param - reference to {@link android.net.wifi.WifiManager}
     */
    private val wifiManager: WifiManager,

    /**
     * we use to run {@link android.app.Activity.startActivityForResult } on android 30+ versions
     */
    private val startActivityForResult: (Intent, Int) -> Unit
) {

    /**
     * We use cache to avoid creating duplicate instances for the same config
     */
    private val cache = HashMap<WifiConfig, ConnectionDelegate>()

    /**
     *
     * Provide {@link io.connect.wifi.sdk.connect.delegate.ConnectionDelegate}
     * by {@link io.connect.wifi.sdk.config.WifiConfig} or null
     *
     * @param - {@link io.connect.wifi.sdk.config.WifiConfig}
     * @return - implementation of {@link io.connect.wifi.sdk.connect.delegate.ConnectionDelegate} or null
     *
     *
     * @see io.connect.wifi.sdk.connect.delegate.ConnectionDelegate
     */
    @SuppressLint("NewApi")
    fun provideDelegate(config: WifiConfig): ConnectionDelegate? {

        val cached: ConnectionDelegate? = cache[config]
        if (cached != null) return cached

        return when (config) {
            is WifiConfig.SupportNetworkWep -> WebDelegate(wifiManager, config).also {
                cache[config] = it
            }
            is WifiConfig.SupportNetworkWpa2 -> Wpa2Delegate(wifiManager, config).also {
                cache[config] = it
            }
            is WifiConfig.SupportNetworkWpa2Eap -> Wpa2EapDelegate(wifiManager, config).also {
                cache[config] = it
            }
            is WifiConfig.Wpa2PassphraseSuggestion -> SuggestionDelegate(wifiManager, config).also {
                cache[config] = it
            }
            is WifiConfig.SuggestionNetworkList -> SuggestionListDelegate(
                config,
                startActivityForResult
            ).also {
                cache[config] = it
            }
            else -> null
        }
    }
}