package io.connect.wifi.sdk.config

/**
 * @suppress Internal api
 *
 * Types of configurations
 *
 * @since 1.0.1
 */
internal sealed class WifiConfig(open val ssid: String? = null) {

    data class PasspointConfiguration(
        val password: String,
        val fqdn: String,
        val username: String,
        val eapType: String,
        val nonEapInnerMethod: String,
        val friendlyName: String,
        val realm: String,
        val caCertificate: String
    ) : WifiConfig()

    data class EnterpriseSuggestionConfiguration(
        override val ssid: String,
        val password: String,
        val identity: String,
        val caCertificate: String
    ) : WifiConfig(ssid)

    /**
     * We use this configuration for android 29 version.
     */
    data class Wpa2PassphraseSuggestion(override val ssid: String, val password: String) :
        WifiConfig(ssid)

    /**
     * We use this configuration for android 30 and above version.
     */
    @Deprecated("Unused")
    data class SuggestionNetworkList(override val ssid: String, val password: String) :
        WifiConfig(ssid)

    /**
     * We use this configuration for android 28 and lower version.
     * Its wrapper for NetworkEncryption.WEP
     *
     * @see io.connect.wifi.sdk.NetworkEncryption.WEP
     */
    @Deprecated("Unused")
    data class SupportNetworkWep(
        override val ssid: String,
        val password: String,
        val isHidden: Boolean
    ) : WifiConfig(ssid)

    /**
     * We use this configuration for android 28 and lower version.
     * Its wrapper for NetworkEncryption.WPA2
     *
     * @see io.connect.wifi.sdk.NetworkEncryption.WPA2
     */
    data class SupportNetworkWpa2(
        override val ssid: String,
        val password: String,
        val isHidden: Boolean
    ) : WifiConfig(ssid)

    /**
     * We use this configuration for android 28 and lower version.
     * Its wrapper for NetworkEncryption.WPA2EAP
     *
     * @see io.connect.wifi.sdk.NetworkEncryption.WPA2EAP
     */
    @Deprecated("Unused")
    data class SupportNetworkWpa2Eap(
        override val ssid: String,
        val password: String,
        val isHidden: Boolean,
        val identity: String,
        val anonymousIdentity: String,
        val eap: Int,
        val phase2: Int
    ) : WifiConfig(ssid)


}