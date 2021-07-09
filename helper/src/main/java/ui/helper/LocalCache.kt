package ui.helper

import android.content.Context

class LocalCache(context: Context) {

    private val preferences = context.getSharedPreferences("demo_cache", Context.MODE_PRIVATE)

    companion object {

        private const val KEY_PASS = "user_pass"
        private const val KEY_SSID = "key_ssid"
        private const val KEY_API_TOKEN = "KEY_API_TOKEN"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_CHANNEL_ID = "KEY_CHANNEL_ID"
        private const val KEY_PROJECT_ID = "KEY_PROJECT_ID"

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

    var apiToken: String
        set(value) {
            preferences.edit().putString(KEY_API_TOKEN, value).apply()
        }
        get() {
            return preferences.getString(KEY_API_TOKEN, "") ?: ""
        }

    var userId: String
        set(value) {
            preferences.edit().putString(KEY_USER_ID, value).apply()
        }
        get() {
            return preferences.getString(KEY_USER_ID, "") ?: ""
        }

    var channelId: String
        set(value) {
            preferences.edit().putString(KEY_CHANNEL_ID, value).apply()
        }
        get() {
            return preferences.getString(KEY_CHANNEL_ID, "") ?: ""
        }

    var projectId: String
        set(value) {
            preferences.edit().putString(KEY_PROJECT_ID, value).apply()
        }
        get() {
            return preferences.getString(KEY_PROJECT_ID, "") ?: ""
        }

}