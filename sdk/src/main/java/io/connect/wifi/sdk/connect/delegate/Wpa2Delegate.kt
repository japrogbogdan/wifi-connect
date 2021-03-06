package io.connect.wifi.sdk.connect.delegate

import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.config.WifiConfig

/**
 * @suppress Internal api
 *
 * Delegate implementation for WifiConfig.SupportNetworkWpa2
 *
 * @see io.connect.wifi.sdk.config.WifiConfig.SupportNetworkWpa2
 *
 * @since 1.0.1
 */
internal class Wpa2Delegate(
    wifiManager: WifiManager,
    connectivityManager: ConnectivityManager,
    private val rule: WifiConfig.SupportNetworkWpa2,
    status: (ConnectStatus) -> Unit
) : BaseSupportDelegate(wifiManager, connectivityManager, status) {

    /**
     * Connect to wifi using previously created delegate implementation
     */
    override fun connect() {
        config?.let {
            it.SSID = quoteNonHex(rule.ssid)
            it.hiddenSSID = rule.isHidden
            it.preSharedKey = quoteNonHex(rule.password, 64)
            it.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            it.allowedProtocols.set(WifiConfiguration.Protocol.WPA) // For WPA
            it.allowedProtocols.set(WifiConfiguration.Protocol.RSN) // For WPA2
            it.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            it.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
            it.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            it.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            it.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            it.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        }
        updateNetwork()
    }

    override fun toString(): String {
        return "Wpa2Delegate(rule=$rule)"
    }


}