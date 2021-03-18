package io.connect.wifi.sdk.connect.delegate

import android.annotation.SuppressLint
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.P)
abstract class BaseSupportDelegate(private val wifiManager: WifiManager) : ConnectionDelegate {

    protected var config: WifiConfiguration? = null
    private val HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+")

    override fun prepareDelegate() {
        connectToWifi()
        changeNetworkCommon()
    }

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

    private fun changeNetworkCommon() {
        config = WifiConfiguration().apply {
            allowedAuthAlgorithms.clear()
            allowedGroupCiphers.clear()
            allowedKeyManagement.clear()
            allowedPairwiseCiphers.clear()
            allowedProtocols.clear()
        }
    }

    protected fun updateNetwork(){
        findNetworkInExistingConfig()?.let {
            wifiManager.removeNetwork(it)
            wifiManager.saveConfiguration()
        }

        val networkId = wifiManager.addNetwork(config)
        if (networkId >= 0) {
            // Try to disable the current network and start a new one.
            if (wifiManager.enableNetwork(networkId, true)) {
                wifiManager.saveConfiguration()
            }
        }
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