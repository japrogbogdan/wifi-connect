package io.connect.wifi.sample.cache

import android.content.Context

class LocalCache(context: Context) {

    private val preferences = context.getSharedPreferences("ll_cache", Context.MODE_PRIVATE)

    companion object {

        private const val KEY_PASS = "user_pass"

        private const val KEY_SSID = "key_ssid"

    }

    var userPass: String
        set(value) {
            preferences.edit().putString(KEY_PASS, value).apply()
        }
        get() {
            return preferences.getString(KEY_PASS, "") ?: ""
        }


    var ssid: String
        set(value) {
            preferences.edit().putString(KEY_SSID, value).apply()
        }
        get() {
            return preferences.getString(KEY_SSID, "") ?: ""
        }

}