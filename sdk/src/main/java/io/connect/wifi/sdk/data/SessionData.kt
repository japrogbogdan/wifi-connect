package io.connect.wifi.sdk.data

import io.connect.wifi.sdk.analytics.ConnectResult
import org.json.JSONArray
import org.json.JSONObject

internal data class SessionData(
    val apiKey: String,
    val channelId: Int,
    val projectId: Int,
    val userId: String,
    var traceId: String?
) {

    fun toJsonBody(info: DeviceData): String {
        val fields = JSONObject().apply {
            put("platform", info.platform)
            put("platform_version", info.platformVersion)
            put("model", info.model)
            put("vendor", info.vendor)
            put("hs20_support", info.supportHs20.toString())
        }
        val json = JSONObject().apply {
            put("user_id", userId)
            put("user_field", fields)
        }
        return json.toString()
    }

    fun toConnectionResultBody(info: DeviceData, result: List<ConnectResult>): String {
        val logs = JSONArray()
        result.forEach { conn ->
            JSONObject().apply {
                put("method", conn.rule.ruleName.orEmpty())
                put("status", conn.status.infoName)
                conn.raw?.let { error ->
                    val end = if (error.length > 4096) 4095 else error.length - 1
                    put("raw_error_msg", error.substring(0..end))
                }
            }.also {
                logs.put(it)
            }
        }
        val json = JSONObject().apply {
            put("platform", info.platform)
            put("platform_version", info.platformVersion)
            put("model", info.model)
            put("vendor", info.vendor)
            put("hs20_support", info.supportHs20)
            put("user_id", userId)
            put("trace_id", traceId.orEmpty())
            put("logs", logs)
        }
        return json.toString()
    }
}
