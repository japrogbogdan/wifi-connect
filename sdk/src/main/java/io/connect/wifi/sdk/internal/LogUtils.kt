package io.connect.wifi.sdk.internal

import android.util.Log
import io.connect.wifi.sdk.BuildConfig

internal object LogUtils {

    private const val TAG = "SmartWiFiSDK"

    fun debug(text: String, error: Throwable? = null) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, text, error)
    }
}