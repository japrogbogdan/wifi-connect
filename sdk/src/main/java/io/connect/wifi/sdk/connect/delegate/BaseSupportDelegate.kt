package io.connect.wifi.sdk.connect.delegate

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import io.connect.wifi.sdk.ConnectStatus
import java.util.regex.Pattern

/**
 * @suppress Internal api
 *
 * Base implementation for all delegates that we can use on android 28 and lower versions.
 *
 * @since 1.0.1
 */
internal abstract class BaseSupportDelegate(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager,
    private val status: ((ConnectStatus) -> Unit)? = null
) : ConnectionDelegate {
    /**
     * We use [android.net.wifi.WifiConfiguration] to connect to wifi
     */
    protected var config: WifiConfiguration? = null
    private val HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+")

    /**
     * Make delegate implementation available for connection
     */
    override fun prepareDelegate() {
        status?.invoke(ConnectStatus.Processing)
        connectToWifi()
        changeNetworkCommon()
    }

    /**
     * Enable wifi if disabled
     */
    private fun connectToWifi() {
        // Start WiFi, otherwise nothing will work
        if (!wifiManager.isWifiEnabled) {
            if (!wifiManager.setWifiEnabled(true)) {
                return
            }
            // This happens very quickly, but need to wait for it to enable. A little busy wait?
            var count = 0
            while (!wifiManager.isWifiEnabled) {
                if (count >= 10) {
                    return
                }

                try {
                    Thread.sleep(1000L)
                } catch (ie: InterruptedException) {
                    // continue
                }
                count++
            }
        }
    }

    /**
     * Reset all configs
     */
    private fun changeNetworkCommon() {
        config = WifiConfiguration().apply {
            allowedAuthAlgorithms.clear()
            allowedGroupCiphers.clear()
            allowedKeyManagement.clear()
            allowedPairwiseCiphers.clear()
            allowedProtocols.clear()
        }
    }

    /**
     * Do connection by config
     */
    protected fun updateNetwork() {
        findNetworkInExistingConfig()?.let {
            wifiManager.removeNetwork(it)
            wifiManager.saveConfiguration()
        }

        val networkId = wifiManager.addNetwork(config)
        if (networkId >= 0) {
            wifiManager.disconnect()
            // Try to disable the current network and start a new one.
            if (wifiManager.enableNetwork(networkId, true)) {//true
                wifiManager.saveConfiguration()
            }

            wifiManager.reconnect()

            var count = 0
            while (count < 5 && (!wifiManager.isWifiEnabled || wifiManager.connectionInfo.ssid != config?.SSID)) {
                try {
                    Thread.sleep(2000L)
                } catch (ie: InterruptedException) {
                    // continue
                }
                count++
            }

            if (wifiManager.isWifiEnabled &&
                wifiManager.connectionInfo.ssid == config?.SSID &&
                isConnectWiFi(connectivityManager)
            )
                status?.invoke(ConnectStatus.Success)
            else
                status?.invoke(ConnectStatus.Error(Exception("Can't enable wifi ssid network")))
        } else status?.invoke(ConnectStatus.Error(Exception("Can't add network")))
    }

    @Suppress("DEPRECATION")
    fun isConnectWiFi(connectivityManager: ConnectivityManager?): Boolean {
        var result = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.run {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        else -> false
                    }
                }
            }
        } else {
            connectivityManager?.run {
                connectivityManager.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI && isConnected) {
                        result = true
                    } else {
                        result = true
                    }
                }
            }
        }
        return result
    }

    @SuppressLint("MissingPermission")
    private fun findNetworkInExistingConfig(): Int? {
        for (existingConfig in wifiManager.configuredNetworks) {
            if (existingConfig.SSID != null && existingConfig.SSID == config?.SSID)
                return existingConfig.networkId
        }
        return null
    }

    protected fun quoteNonHex(value: String, vararg allowedLengths: Int): String? {
        return if (isHexOfLength(value, *allowedLengths)) value
        else convertToQuotedString(value)
    }

    private fun isHexOfLength(value: CharSequence?, vararg allowedLengths: Int): Boolean {
        if (value == null || !HEX_DIGITS.matcher(value).matches()) {
            return false
        }
        if (allowedLengths.size == 0) {
            return true
        }
        for (length in allowedLengths) {
            if (value.length == length) {
                return true
            }
        }
        return false
    }

    private fun convertToQuotedString(s: String?): String? {
        if (s == null || s.isEmpty()) {
            return null
        }
        // If already quoted, return as-is
        return if (s[0] == '"' && s[s.length - 1] == '"') {
            s
        } else '\"'.toString() + s + '\"'
    }
}