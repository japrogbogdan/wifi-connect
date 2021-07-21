package io.connect.wifi.sdk.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.connect.wifi.sdk.data.DeviceData

/**
 * @suppress Internal api
 *
 * Builder from current device dump
 */
internal class DeviceDump(private val context: Context) {

    fun getDataDump(): DeviceData {
        val hasPassPoint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT)
        } else false

        return DeviceData(
            platform = "android",
            platformVersion = Build.VERSION.SDK_INT.toString(),
            model = Build.MODEL,
            vendor = Build.MANUFACTURER,
            supportHs20 = hasPassPoint
        )
    }
}