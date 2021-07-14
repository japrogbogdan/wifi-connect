package io.connect.wifi.sdk

import android.util.Log

internal object LogUtils {

    private const val TAG = "SmartWiFiSDK"

    fun debug(text: String, error: Throwable? = null) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, text, error)
    }
}