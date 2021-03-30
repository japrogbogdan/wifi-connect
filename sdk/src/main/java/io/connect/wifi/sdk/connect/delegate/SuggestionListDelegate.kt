package io.connect.wifi.sdk.connect.delegate

import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.config.WifiConfig

@RequiresApi(Build.VERSION_CODES.Q)
internal class SuggestionListDelegate(
    private val rule: WifiConfig.SuggestionNetworkList,
    private val startActivityForResult: (Intent, Int) -> Unit
) : ConnectionDelegate {

    private val suggestion = WifiNetworkSuggestion.Builder().apply {
        setPriority(Int.MAX_VALUE)
        setSsid(rule.ssid)
        setWpa2Passphrase(rule.password)
    }
    private val suggestions = ArrayList<WifiNetworkSuggestion>()

    /**
     * Make delegate implementation available for connection
     */
    override fun prepareDelegate() {
        suggestions.clear()
        suggestions.add(suggestion.build())
    }

    /**
     * Connect to wifi using previously created delegate implementation
     */
    override fun connect() {
        val bundle = Bundle().apply {
            putParcelableArrayList(Settings.EXTRA_WIFI_NETWORK_LIST, suggestions)
        }
        val intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS).apply {
            putExtras(bundle)
        }
        startActivityForResult(intent, 0)
    }
}