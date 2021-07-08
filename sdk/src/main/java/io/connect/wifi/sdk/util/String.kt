package io.connect.wifi.sdk.util

import io.connect.wifi.sdk.WifiRule
import org.json.JSONObject

internal fun String.toWifiRules(): List<WifiRule> {
    val json = JSONObject(this)
    val configs = json.getJSONObject("wifi_configs")
    println("Parse json: $json")
    return emptyList()
}