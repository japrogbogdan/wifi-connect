package io.connect.wifi.sdk.connect.delegate

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.config.WifiConfig

/**
 * @suppress Internal api
 *
 * Delegate implementation for WifiConfig.SupportNetworkWep
 *
 * @see io.connect.wifi.sdk.config.WifiConfig.SupportNetworkWep
 *
 * @since 1.0.1
 */
@RequiresApi(Build.VERSION_CODES.P)
internal class WebDelegate(
    wifiManager: WifiManager,
    private val rule: WifiConfig.SupportNetworkWep
) : BaseSupportDelegate(wifiManager) {

    /**
     * Connect to wifi using previously created delegate implementation
     */
    override fun connect() {
        config?.let {
            it.SSID = quoteNonHex(rule.ssid)
            it.hiddenSSID = rule.isHidden
            it.wepKeys[0] = quoteNonHex(rule.password, 10, 26, 58)
            it.wepTxKeyIndex = 0
            it.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
            it.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            it.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            it.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            it.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            it.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
        }
        updateNetwork()
    }
}