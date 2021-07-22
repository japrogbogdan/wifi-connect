package io.connect.wifi.sdk

import android.content.Context
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.session.SessionExecutor
import io.connect.wifi.sdk.util.DeviceDump
import java.lang.Exception

/**
 * Connect to wifi command
 */
class WifiSession private constructor(
    /**
     * Context of current entry point of the app
     */
    val context: Context,
    /**
     * unique identifier for the partner access to api for fetching rules
     */
    val apiKey: String,
    /**
     * unique identifier of current user (app install)
     */
    val userId: String,
    /**
     * identifier of the channel
     */
    val channelId: Int,
    /**
     * identifier of the project
     */
    val projectId: Int,
    /**
     * Enable/disable internal delivery of success callbacks
     */
    val autoDeliverSuccessCallback: Boolean,
    /**
     * callback for status changes
     */
    val callback: WifiSessionCallback?
) {

    private var session: SessionExecutor? = null

    /**
     * Begin connection session for current device dump & user settings (apiKey, userId, channelId, projectId)
     */
    fun startSession() {
        val data = SessionData(apiKey, channelId, projectId, userId, null)
        val dump = DeviceDump(context).getDataDump()
        LogUtils.debug("[WifiSession] Start session:\ndata:$data\ndump:$dump")
        session = SessionExecutor(context, data, dump, callback, autoDeliverSuccessCallback)
        session?.start()
    }

    /**
     * Cancel existing session if you want interrupt connect to wifi.
     * This will not disconnect us from wifi. This is for stopping connection attempt
     * if any still processing (we don't have success or fail response).
     */
    fun cancelSession() {
        LogUtils.debug("[WifiSession] Cancel session")
        session?.cancel()
        session = null
    }

    data class Builder(
        /**
         * Context of current entry point of the app
         */
        val context: Context,
        /**
         * unique identifier for the partner access to api for fetching rules
         */
        var apiKey: String? = null,
        /**
         * unique identifier of current user (app install)
         */
        var userId: String? = null,
        /**
         * identifier of the channel
         */
        var channelId: Int = 0,
        /**
         * identifier of the project
         */
        var projectId: Int = 0,
        /**
         * Enable/disable internal delivery of success callbacks
         */
        var autoDeliverSuccessCallback: Boolean = true,
        /**
         * callback for status changes
         */
        var callback: WifiSessionCallback? = null
    ) {

        /**
         * unique identifier for the partner access to api for fetching rules
         */
        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }

        /**
         * unique identifier of current user (app install)
         */
        fun userId(userId: String) = apply { this.userId = userId }

        /**
         * identifier of the channel
         */
        fun channelId(channelId: Int) = apply { this.channelId = channelId }

        /**
         * identifier of the project
         */
        fun projectId(projectId: Int) = apply { this.projectId = projectId }

        /**
         * Enable/disable internal delivery of success callbacks
         */
        fun autoDeliverSuccessCallback(autoDeliverSuccessCallback: Boolean) = apply { this.autoDeliverSuccessCallback = autoDeliverSuccessCallback }

        /**
         * callback for status changes
         */
        fun statusCallback(callback: WifiSessionCallback) = apply { this.callback = callback }

        /**
         * create new WifiSession instance with provided earlier data
         */
        fun create() =
            WifiSession(context, apiKey.orEmpty(), userId.orEmpty(), channelId, projectId, autoDeliverSuccessCallback, callback)
    }
}

/**
 * Listener for session status changes
 */
interface WifiSessionCallback {

    /**
     * Status had changed
     */
    fun onStatusChanged(newStatus: WiFiSessionStatus)
}

/**
 * Status for connection session
 */
sealed class WiFiSessionStatus {

    /**
     * Requesting remote rules for current user & it's device
     */
    object RequestConfigs : WiFiSessionStatus() {
        override fun toString() = "RequestConfigs"
    }

    /**
     * We fetched wifi rules & now in the process of connecting to wifi
     */
    object Connecting : WiFiSessionStatus() {
        override fun toString() = "Connecting"
    }

    /**
     * We had sent the wifi connection request to device's OS & had received positive response.
     */
    object Success : WiFiSessionStatus() {
        override fun toString() = "Success"
    }

    /**
     * There's an failure. See reason for explanation.
     */
    data class Error(val reason: Exception) : WiFiSessionStatus()

    /**
     * Current session had been canceled by user request.
     */
    object CancelSession : WiFiSessionStatus() {
        override fun toString() = "CancelSession"
    }
}