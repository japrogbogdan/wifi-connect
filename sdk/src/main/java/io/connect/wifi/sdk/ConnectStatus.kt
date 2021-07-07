package io.connect.wifi.sdk

sealed class ConnectStatus {

    object Processing : ConnectStatus()

    object Success : ConnectStatus()

    data class Error(val reason: Exception) : ConnectStatus()

}
