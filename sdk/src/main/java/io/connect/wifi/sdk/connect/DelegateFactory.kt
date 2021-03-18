package io.connect.wifi.sdk.connect

import android.annotation.SuppressLint
import android.content.Intent
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.WifiRule
import io.connect.wifi.sdk.config.WifiConfig
import io.connect.wifi.sdk.connect.delegate.*
import io.connect.wifi.sdk.connect.delegate.SuggestionDelegate
import io.connect.wifi.sdk.connect.delegate.WebDelegate
import io.connect.wifi.sdk.connect.delegate.Wpa2Delegate
import io.connect.wifi.sdk.connect.delegate.Wpa2EapDelegate

internal class DelegateFactory(private val wifiManager: WifiManager, private val startActivityForResult: (Intent, Int) -> Unit) {

    private val cache = HashMap<WifiConfig, ConnectionDelegate>()

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
            is WifiConfig.SuggestionNetworkList -> SuggestionListDelegate(config, startActivityForResult).also {
                cache[config] = it
            }
            else -> null
        }
    }
}