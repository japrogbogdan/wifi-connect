package io.connect.wifi.sdk.connect.delegate

import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.config.WifiConfig

/**
 * @suppress Internal api
 *
 * Delegate implementation for WifiConfig.Wpa2Api30
 *
 * @see io.connect.wifi.sdk.config.WifiConfig.Wpa2Api30
 *
 * @since 1.0.1
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class Wpa2Api30Delegate(
    private val rule: WifiConfig.Wpa2Api30,
    private val status: (ConnectStatus) -> Unit,
    private val startActivityForResult: (Intent, Int) -> Unit
) : ConnectionDelegate {

    private val suggestion = WifiNetworkSuggestion.Builder().apply {
        setPriority(Int.MAX_VALUE) //500
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
        status.invoke(ConnectStatus.Processing)
        val bundle = Bundle().apply {
            putParcelableArrayList(Settings.EXTRA_WIFI_NETWORK_LIST, suggestions)
        }
        val intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS).apply {
            putExtras(bundle)
        }
        startActivityForResult(intent, 0)
        status.invoke(ConnectStatus.Success)
    }

    override fun toString(): String {
        return "Wpa2Api30Delegate(rule=$rule)"
    }


}