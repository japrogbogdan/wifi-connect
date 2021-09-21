package io.connect.wifi.sdk.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.connect.wifi.sdk.WifiRule
import java.lang.reflect.Type


class LocalCache(context: Context) {

    private val preferences = context.getSharedPreferences("sdk_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_LIST_WIFI_RULE = "KEY_LIST_WIFI_RULE"
        private const val KEY_TRACE_ID = "KEY_TRACE_ID"
    }

    var listWiFiRule: ArrayList<WifiRule>?
        set(value) {
            value?.let { data ->
                if(data.isNotEmpty()){
                    val inString = gson.toJson(data)
                    preferences.edit().putString(KEY_LIST_WIFI_RULE, inString).apply()
                } else
                    preferences.edit().remove(KEY_LIST_WIFI_RULE).commit()
            } ?: run {
                preferences.edit().remove(KEY_LIST_WIFI_RULE).commit()
            }
        }
        get() {
            preferences.getString(KEY_LIST_WIFI_RULE, null)?.let { json ->
                val type: Type = object : TypeToken<ArrayList<WifiRule>?>() {}.getType()
                try {
                    return gson.fromJson(json, type)
                } catch (er: JsonSyntaxException) {
                    return null
                }
            } ?: run {
                return null
            }
        }

    var traceId: String?
        set(value) {
            value?.let { data ->
                preferences.edit().putString(KEY_TRACE_ID, data).apply()
            } ?: run {
                preferences.edit().remove(KEY_TRACE_ID).commit()
            }
        }
        get() {
            return preferences.getString(KEY_TRACE_ID, null)
        }
}