package io.connect.wifi.sdk

import androidx.annotation.Keep
import kotlinx.serialization.Serializable


/**
 * Types of encryption for wifi point
 *
 * @since 1.0.1
 */
@Serializable
sealed class NetworkEncryption {
    /**
     * @suppress Not Recommended option.
     * Can use WifiConfiguration.AuthAlgorithm.SHARED authentication
     * protocols for wifi communications
     *
     * @see android.net.wifi.WifiConfiguration.AuthAlgorithm.SHARED
     */
    @Keep
    object WEP : NetworkEncryption()

    /**
     * Can use WifiConfiguration.AuthAlgorithm.OPEN authentication
     * protocols for wifi communications.
     * It supports WifiConfiguration.Protocol.WPA and
     * WifiConfiguration.Protocol.RSN protocols
     *
     * @see android.net.wifi.WifiConfiguration.AuthAlgorithm.OPEN
     * @see android.net.wifi.WifiConfiguration.Protocol.WPA
     * @see android.net.wifi.WifiConfiguration.Protocol.RSN
     */
    @Keep
    object WPA2 : NetworkEncryption()

    /**
     * Can use WifiConfiguration.AuthAlgorithm.OPEN authentication
     * protocols for wifi communications.
     * It supports WifiConfiguration.Protocol.RSN protocol.
     * It uses WifiEnterpriseConfig
     *
     * @see android.net.wifi.WifiConfiguration.AuthAlgorithm.OPEN
     * @see android.net.wifi.WifiConfiguration.Protocol.RSN
     * @see android.net.wifi.WifiEnterpriseConfig
     */
    @Keep
    object WPA2EAP : NetworkEncryption()

    /**
     * @suppress Unused option
     */
    @Keep
    object NO_PASSWORD : NetworkEncryption()
}