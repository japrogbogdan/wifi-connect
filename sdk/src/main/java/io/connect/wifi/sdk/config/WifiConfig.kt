package io.connect.wifi.sdk.config

internal sealed class WifiConfig(open val ssid: String) {

    data class Wpa2PassphraseSuggestion(override val ssid: String, val password: String) :
        WifiConfig(ssid)

    data class SuggestionNetworkList(override val ssid: String, val password: String) :
        WifiConfig(ssid)

    data class SupportNetworkWep(
        override val ssid: String,
        val password: String,
        val isHidden: Boolean
    ) : WifiConfig(ssid)

    data class SupportNetworkWpa2(
        override val ssid: String,
        val password: String,
        val isHidden: Boolean
    ) : WifiConfig(ssid)

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