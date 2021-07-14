package io.connect.wifi.sdk.util

import io.connect.wifi.sdk.Constants
import io.connect.wifi.sdk.WifiRule
import org.json.JSONException
import org.json.JSONObject

internal fun String.toWifiRules(): Pair<List<WifiRule>,String?> {
    val json = JSONObject(this)
    val configs = json.getJSONObject("wifi_configs")
    var config: JSONObject? = null
    var configItem: JSONObject? = null
    var rule: WifiRule? = null
    val rules = mutableListOf<WifiRule>()
    for (i in 0..10) {
        config = try {
            configs.getJSONObject(i.toString())
        } catch (e: JSONException) {
            null
        }
        if (config == null) break
        else {
            rule = try {
                when {

                    config.has(Constants.TYPE_PASSPOINT_AOUCP) -> {

                        configItem = config.getJSONObject(Constants.TYPE_PASSPOINT_AOUCP)

                        WifiRule.Builder()
                            .ruleName(Constants.TYPE_PASSPOINT_AOUCP)
                            .caCertificate(configItem.getString("caCertificate"))
                            .eapType(configItem.getString("eapType"))
                            .fqdn(configItem.getString("fqdn"))
                            .friendlyName(configItem.getString("friendlyName"))
                            .nonEapInnerMethod(configItem.getString("nonEapInnerMethod"))
                            .password(configItem.getString("password"))
                            .realm(configItem.getString("realm"))
                            .username(configItem.getString("username"))
                            .build()
                    }

                    config.has(Constants.TYPE_WPA2_ENTERPRISE_SUGGESTION) -> {

                        configItem = config.getJSONObject(Constants.TYPE_WPA2_ENTERPRISE_SUGGESTION)

                        WifiRule.Builder()
                            .ruleName(Constants.TYPE_WPA2_ENTERPRISE_SUGGESTION)
                            .caCertificate(configItem.getString("caCertificate"))
                            .identity(configItem.getString("identity"))
                            .password(configItem.getString("password"))
                            .ssid(configItem.getString("ssid"))
                            .build()
                    }

                    config.has(Constants.TYPE_WPA2_SUPPORT) -> {

                        configItem = config.getJSONObject(Constants.TYPE_WPA2_SUPPORT)

                        WifiRule.Builder()
                            .ruleName(Constants.TYPE_WPA2_SUPPORT)
                            .password(configItem.getString("password"))
                            .ssid(configItem.getString("ssid"))
                            .build()
                    }

                    config.has(Constants.TYPE_WPA2_SUGGESTION) -> {

                        configItem = config.getJSONObject(Constants.TYPE_WPA2_SUGGESTION)

                        WifiRule.Builder()
                            .ruleName(Constants.TYPE_WPA2_SUGGESTION)
                            .password(configItem.getString("password"))
                            .ssid(configItem.getString("ssid"))
                            .build()
                    }

                    else -> null
                }
            } catch (e: JSONException) {
                null
            }
            rule?.let { rules.add(it) }
        }
    }
    val traceId = json.getString("trace_id")
    return Pair(rules, traceId)
}