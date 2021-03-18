package io.connect.wifi.sdk.connect.delegate

import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.config.WifiConfig

@RequiresApi(Build.VERSION_CODES.Q)
internal class SuggestionDelegate(
    private val wifiManager: WifiManager,
    private val rule: WifiConfig.Wpa2PassphraseSuggestion
) : ConnectionDelegate {

    private val suggestion = WifiNetworkSuggestion.Builder().apply {
        setPriority(Int.MAX_VALUE)
        setSsid(rule.ssid)
        setWpa2Passphrase(rule.password)
    }
    private val suggestions = ArrayList<WifiNetworkSuggestion>()

    override fun prepareDelegate() {
        suggestions.clear()
        suggestions.add(suggestion.build())
    }

    override fun connect() {
        wifiManager.removeNetworkSuggestions(suggestions)
        val status = wifiManager.addNetworkSuggestions(suggestions)
    }
}