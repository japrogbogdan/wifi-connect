package io.connect.wifi.sdk

/**
 * Status for connection attempt
 */
sealed class ConnectStatus {

    /**
     * Doing hard work to connect to wifi
     */
    object Processing : ConnectStatus() {
        override fun toString() = "Processing"
    }

    /**
     * Finished connection attempt. System had accepted out request & replied with success.
     */
    object Success : ConnectStatus() {
        override fun toString() = "Success"
    }

    /**
     * Finished connection attempt with fail. We have reason for failure.
     */
    data class Error(val reason: Exception) : ConnectStatus()

}
