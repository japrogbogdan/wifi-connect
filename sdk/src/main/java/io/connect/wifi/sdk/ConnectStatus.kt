package io.connect.wifi.sdk

internal sealed class ConnectStatus {

    object Processing : ConnectStatus()

    object Success : ConnectStatus()

    data class Error(val reason: Exception) : ConnectStatus()

}
