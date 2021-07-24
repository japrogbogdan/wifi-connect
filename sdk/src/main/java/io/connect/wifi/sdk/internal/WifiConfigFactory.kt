package io.connect.wifi.sdk.internal

import android.os.Build
import io.connect.wifi.sdk.NetworkEncryption
import io.connect.wifi.sdk.WifiRule
import io.connect.wifi.sdk.internal.Constants.Companion.TYPE_PASSPOINT_AOUCP
import io.connect.wifi.sdk.internal.Constants.Companion.TYPE_PASSPOINT_RESULT
import io.connect.wifi.sdk.internal.Constants.Companion.TYPE_WPA2_ENTERPRISE_SUGGESTION
import io.connect.wifi.sdk.internal.Constants.Companion.TYPE_WPA2_SUGGESTION
import io.connect.wifi.sdk.internal.Constants.Companion.TYPE_WPA2_SUPPORT
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
            (rule.ruleName == TYPE_WPA2_SUPPORT) -> {
                rule.ssid?.let { id ->
                    rule.password?.let { pass ->
                        WifiConfig.SupportNetworkWpa2(id, pass, rule.hidden).also {
                            cache[rule] = it
                        }
                    }
                }
            }
            (rule.ruleName == TYPE_WPA2_SUGGESTION) -> {
                rule.ssid?.let { id ->
                    rule.password?.let { pass ->
                        WifiConfig.Wpa2PassphraseSuggestion(id, pass).also {
                            cache[rule] = it
                        }
                    }
                }
            }
            (rule.ruleName == TYPE_PASSPOINT_AOUCP) -> {
                rule.password?.let { password ->
                    rule.fqdn?.let { fqdn ->
                        rule.username?.let { username ->
                            rule.eapType?.let { eapType ->
                                rule.nonEapInnerMethod?.let { nonEapInnerMethod ->
                                    rule.friendlyName?.let { friendlyName ->
                                        rule.realm?.let { realm ->
                                            rule.caCertificate?.let { certificate ->
                                                WifiConfig.PasspointAddOrUpdateConfiguration(
                                                    password = password,
                                                    fqdn = fqdn,
                                                    username = username,
                                                    eapType = eapType,
                                                    nonEapInnerMethod = nonEapInnerMethod,
                                                    friendlyName = friendlyName,
                                                    realm = realm,
                                                    caCertificate = certificate
                                                ).also {
                                                    cache[rule] = it
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            (rule.ruleName == TYPE_PASSPOINT_RESULT) -> {
                rule.password?.let { password ->
                    rule.fqdn?.let { fqdn ->
                        rule.username?.let { username ->
                            rule.eapType?.let { eapType ->
                                rule.nonEapInnerMethod?.let { nonEapInnerMethod ->
                                    rule.friendlyName?.let { friendlyName ->
                                        rule.realm?.let { realm ->
                                            rule.caCertificate?.let { certificate ->
                                                WifiConfig.PasspointResultConfiguration(
                                                    password = password,
                                                    fqdn = fqdn,
                                                    username = username,
                                                    eapType = eapType,
                                                    nonEapInnerMethod = nonEapInnerMethod,
                                                    friendlyName = friendlyName,
                                                    realm = realm,
                                                    caCertificate = certificate
                                                ).also {
                                                    cache[rule] = it
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            (rule.ruleName == TYPE_WPA2_ENTERPRISE_SUGGESTION) -> {
                rule.password?.let { password ->
                    rule.ssid?.let { ssid ->
                        rule.identity?.let { identity ->
                            rule.caCertificate?.let { certificate ->
                                rule.fqdn?.let { fqdn ->
                                    WifiConfig.EnterpriseSuggestionConfiguration(
                                        ssid = ssid,
                                        password = password,
                                        identity = identity,
                                        caCertificate = certificate,
                                        fqdn = fqdn
                                    ).also {
                                        cache[rule] = it
                                    }
                                }

                            }
                        }
                    }
                }
            }

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