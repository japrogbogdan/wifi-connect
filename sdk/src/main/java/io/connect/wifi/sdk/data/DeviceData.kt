package io.connect.wifi.sdk.data

internal data class DeviceData(
    val platform: String,
    val platformVersion: String,
    val model: String,
    val vendor: String,
    val supportHs20: Boolean
)
