package io.connect.wifi.sdk

import android.content.Context
import android.net.wifi.WifiManager
import androidx.annotation.Keep
import io.connect.wifi.sdk.activity.ActivityHelper
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.internal.LogUtils
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
     * identifier for domain name server of the API SmartWiFi
     */
    var apiDomain: String,
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
    val callback: WifiSessionCallback?,
    /**
     * Holder of activity reference. Used when context is no instance of activity
     */
    val activityHelper: ActivityHelper?
) {

    companion object {
        /*
        * Check On WiFiModule
         */
        fun isWifiEnabled(context: Context) =
            (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled
    }

    private var session: SessionExecutor? = null

    /**
     * Initialization SessionExecutor
     */
    private fun initSessionExecutor() {
        if (session == null) {
            val data = SessionData(apiKey, apiDomain, channelId, projectId, userId, null)
            val dump = DeviceDump(context).getDataDump()
            LogUtils.debug("[WifiSession] Get session config:\ndata:$data\ndump:$dump")
            session = SessionExecutor(
                context,
                data,
                dump,
                callback,
                activityHelper,
                autoDeliverSuccessCallback
            )
        }
    }

    /**
     * Get session config for current device dump & user settings (apiKey, userId, channelId, projectId)
     * save config to cache
     */
    fun getSessionConfig() {
        LogUtils.debug("[WifiSession] Get session config")
        initSessionExecutor()
        session?.requestConfigs()
    }

    /**
     * Begin connection session by config from cache
     */
    fun startSession() {
        LogUtils.debug("[WifiSession] Start session")
        initSessionExecutor()
        session?.startConnection()
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

    @Keep
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
         * identifier for domain name server of the API SmartWiFi
         */
        var apiDomain: String = "",
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
        var callback: WifiSessionCallback? = null,
        /**
         * Holder of activity reference. Used when context is no instance of activity
         */
        var activityHelper: ActivityHelper? = null
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
         * identifier for domain name server of the API SmartWiFi
         */
        fun apiDomain(apiDomain: String) = apply { this.apiDomain = apiDomain }

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
        fun autoDeliverSuccessCallback(autoDeliverSuccessCallback: Boolean) =
            apply { this.autoDeliverSuccessCallback = autoDeliverSuccessCallback }

        /**
         * callback for status changes
         */
        fun statusCallback(callback: WifiSessionCallback) = apply { this.callback = callback }

        /**
         * callback for status changes
         */
        fun activityHelper(activityHelper: ActivityHelper) =
            apply { this.activityHelper = activityHelper }

        /**
         * create new WifiSession instance with provided earlier data
         */
        fun create() =
            WifiSession(
                context,
                apiKey.orEmpty(),
                userId.orEmpty(),
                apiDomain,
                channelId,
                projectId,
                autoDeliverSuccessCallback,
                callback,
                activityHelper
            )
    }
}

/**
 * Listener for session status changes
 */
@Keep
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
    @Keep
    object RequestConfigs : WiFiSessionStatus() {
        override fun toString() = "RequestConfigs"
    }

    /**
     * We received wifi rules
     */
    @Keep
    object ReceivedConfigs : WiFiSessionStatus() {
        override fun toString() = "ReceivedConfigs"
    }

    /**
     * We now in the process of connecting to wifi
     */
    @Keep
    object Connecting : WiFiSessionStatus() {
        override fun toString() = "Connecting"
    }

    /**
     * We had sent the wifi connection request to device's OS & had received positive response.
     */
    @Keep
    object Success : WiFiSessionStatus() {
        override fun toString() = "Success"
    }

    /**
     * There's an failure. See reason for explanation.
     */
    @Keep
    data class Error(val reason: Exception) : WiFiSessionStatus()

    /**
     * Current session had been canceled by user request.
     */
    @Keep
    object CancelSession : WiFiSessionStatus() {
        override fun toString() = "CancelSession"
    }

    /**
     * Сonnection by link started.
     */
    @Keep
    data class ConnectionByLinkSend(val url: String) : WiFiSessionStatus() {
        override fun toString() = "ConnectionByLinkSend"
    }

    /**
     * Сonnection by link is successful.
     */
    @Keep
    data class ConnectionByLinkSuccess(val response: String?) : WiFiSessionStatus()

}