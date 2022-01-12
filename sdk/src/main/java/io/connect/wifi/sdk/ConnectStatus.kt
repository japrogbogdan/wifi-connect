package io.connect.wifi.sdk

import androidx.annotation.Keep

/**
 * Status for connection attempt
 */
sealed class ConnectStatus {

    /**
     * Doing hard work to connect to wifi
     */
    @Keep
    object Processing : ConnectStatus() {
        override fun toString() = "Processing"
    }

    /**
     * Finished connection attempt. System had accepted out request & replied with success.
     */
    @Keep
    object Success : ConnectStatus() {
        override fun toString() = "Success"
    }

    /**
     * Finished connection attempt with fail. We have reason for failure.
     */
    @Keep
    data class Error(val reason: Exception) : ConnectStatus()

    /**
     * Failed to create wifi config by current rule.
     */
    @Keep
    data class CreateWifiConfigError(val reason: Exception) : ConnectStatus()

    /**
     * Unsuccessful wifi point search.
     */
    @Keep
    data class NotFoundWiFiPoint(val ssid: String?) : ConnectStatus()

}
