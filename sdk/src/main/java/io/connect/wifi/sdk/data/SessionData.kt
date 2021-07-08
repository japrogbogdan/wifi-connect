package io.connect.wifi.sdk.data

import org.json.JSONObject

internal data class SessionData(
    val apiKey: String,
    val channelId: Int,
    val projectId: Int,
    val userId: String
) {

    fun toJsonBody(info: DeviceData): String {
        val fields = JSONObject().apply {
            put("platform", info.platform)
            put("platform_version", info.platformVersion)
            put("model", info.model)
            put("vendor", info.vendor)
            put("hs20_support", info.supportHs20)
        }
        val json = JSONObject().apply {
            put("user_id", userId)
            put("user_field", fields)
        }
        return json.toString()
    }
}
