package io.connect.wifi.sdk.util

import android.content.Context
import io.connect.wifi.sdk.WifiRule
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.ArrayList


class LocalCache(context: Context) {

    private val preferences = context.getSharedPreferences("sdk_cache", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LIST_WIFI_RULE = "KEY_LIST_WIFI_RULE"
        private const val KEY_TRACE_ID = "KEY_TRACE_ID"
    }

    var listWiFiRule: ArrayList<WifiRule>?
        set(value) {
            value?.let { data ->
                if(data.isNotEmpty()){
                    val inString = Json.encodeToString(data)
                    preferences.edit().putString(KEY_LIST_WIFI_RULE, inString).apply()
                } else
                    preferences.edit().remove(KEY_LIST_WIFI_RULE)
            } ?: run {
                preferences.edit().remove(KEY_LIST_WIFI_RULE)
            }
        }
        get() {
            preferences.getString(KEY_LIST_WIFI_RULE, null)?.let { json ->
                return Json.decodeFromString<ArrayList<WifiRule>>(json)
            } ?: run {
                return null
            }
        }

    var traceId: String?
        set(value) {
            value?.let { data ->
                preferences.edit().putString(KEY_TRACE_ID, data).apply()
            } ?: run {
                preferences.edit().remove(KEY_TRACE_ID)
            }
        }
        get() {
            return preferences.getString(KEY_TRACE_ID, null)
        }

}