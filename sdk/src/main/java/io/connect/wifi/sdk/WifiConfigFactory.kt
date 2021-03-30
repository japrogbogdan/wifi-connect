package io.connect.wifi.sdk

import android.os.Build
import io.connect.wifi.sdk.config.WifiConfig

/**
 * @suppress Internal api
 *
 * Factory that will create [io.connect.wifi.sdk.config.WifiConfig] for internal usage.
 * @see io.connect.wifi.sdk.config.WifiConfig
 *
 * @since 1.0.1
 */
internal class WifiConfigFactory {
    /**
     * We use cache to avoid creating new [io.connect.wifi.sdk.config.WifiConfig] if already
     * have instance for the same [io.connect.wifi.sdk.WifiRule]
     */
    private val cache = HashMap<WifiRule, WifiConfig>()

    /**
     * Provide [io.connect.wifi.sdk.config.WifiConfig]
     * by [io.connect.wifi.sdk.WifiRule]  or null
     *
     * @param - WifiRule or null
     * @return - WifiConfig or null
     *
     * @see io.connect.wifi.sdk.config.WifiConfig
     * @see io.connect.wifi.sdk.WifiRule
     */
    fun createConfig(rule: WifiRule?): WifiConfig? {

        if (rule == null) return null

        val cached: WifiConfig? = cache[rule]
        if (cached != null) return cached

        return when {
            Build.VERSION.SDK_INT == 29 -> {
                rule.ssid?.let { id ->
                    rule.password?.let { pass ->
                        WifiConfig.Wpa2PassphraseSuggestion(id, pass).also {
                            cache[rule] = it
                        }
                    }
                }
            }
            Build.VERSION.SDK_INT >= 30 -> {
                rule.ssid?.let { id ->
                    rule.password?.let { pass ->
                        WifiConfig.SuggestionNetworkList(id, pass).also {
                            cache[rule] = it
                        }
                    }
                }

            }
            else -> {
                when (rule.networkEncryption) {
                    NetworkEncryption.WEP -> {
                        rule.ssid?.let { id ->
                            rule.password?.let { pass ->
                                WifiConfig.SupportNetworkWep(id, pass, rule.hidden).also {
                                    cache[rule] = it
                                }
                            }
                        }

                    }
                    NetworkEncryption.WPA2 -> {
                        rule.ssid?.let { id ->
                            rule.password?.let { pass ->
                                WifiConfig.SupportNetworkWpa2(id, pass, rule.hidden).also {
                                    cache[rule] = it
                                }
                            }
                        }

                    }
                    NetworkEncryption.WPA2EAP -> {
                        rule.ssid?.let { id ->
                            rule.password?.let { pass ->
                                WifiConfig.SupportNetworkWpa2Eap(
                                    id,
                                    pass,
                                    rule.hidden,
                                    rule.identity ?: "",
                                    rule.anonymousIdentity ?: "",
                                    rule.eapMethod?.id ?: 0,
                                    rule.phase2Method?.id ?: 0
                                ).also {
                                    cache[rule] = it
                                }
                            }
                        }

                    }
                    else -> null
                }
            }
        }
    }

}