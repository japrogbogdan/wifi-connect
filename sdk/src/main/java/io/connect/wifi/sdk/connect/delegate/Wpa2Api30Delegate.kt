package io.connect.wifi.sdk.connect.delegate

import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
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
    private val wifiManager: WifiManager,
    private val rule: WifiConfig.Wpa2Api30,
    private val status: (ConnectStatus) -> Unit
) : ConnectionDelegate {

    private val suggestion = WifiNetworkSuggestion.Builder().apply {
        setPriority(500)
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

        wifiManager.removeNetworkSuggestions(suggestions)

        val status = wifiManager.addNetworkSuggestions(suggestions)
        readStatus(status)
    }

    private fun readStatus(statusCode: Int) {
        when (statusCode) {
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_INVALID")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED")))
            }

            WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS -> {
                status.invoke(ConnectStatus.Success)
            }

            else -> {
                status.invoke(ConnectStatus.Error(Exception("Unknown status: $statusCode")))
            }

        }
    }

    override fun toString(): String {
        return "SuggestionDelegate(rule=$rule)"
    }


}