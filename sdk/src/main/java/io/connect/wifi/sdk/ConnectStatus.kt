package io.connect.wifi.sdk

sealed class ConnectStatus {

    object Processing : ConnectStatus() {
        override fun toString() = "Processing"
    }

    object Success : ConnectStatus() {
        override fun toString() = "Success"
    }

    data class Error(val reason: Exception) : ConnectStatus()

}
