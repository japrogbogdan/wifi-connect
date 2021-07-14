package io.connect.wifi.sdk

import android.util.Log

object LogUtils {

    private const val TAG = "SmartWiFiSDK"

    fun debug(text: String, error: Throwable? = null) {
        Log.e(TAG, text, error)
    }
}