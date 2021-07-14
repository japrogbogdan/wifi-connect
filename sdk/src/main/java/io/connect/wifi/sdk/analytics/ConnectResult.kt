package io.connect.wifi.sdk.analytics

import io.connect.wifi.sdk.WifiRule

internal data class ConnectResult(
    val rule: WifiRule,
    val status: ConnectStatus,
    val raw: String? = null
)

internal enum class ConnectStatus(val infoName: String) {
    Success("Success"), Error("Error")
}
