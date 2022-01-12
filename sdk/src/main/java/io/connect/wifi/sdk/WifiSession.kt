package io.connect.wifi.sdk

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.connect.wifi.sdk.activity.ActivityHelper
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.internal.LogUtils
import io.connect.wifi.sdk.session.SessionExecutor
import io.connect.wifi.sdk.util.DeviceDump
import java.lang.Exception
import java.lang.ref.SoftReference

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

        const val SCAN_WIFI_REQUEST_CODE = 12345

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )
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
        checkAndStartScanWifi() //old session?.startConnection()
    }

    private fun checkAndStartScanWifi() {
        // With Android Level >= 23, you have to ask the user for permission to Call.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { //Build.VERSION_CODES.P 23
            var result: Int
            val listPermissionsNeeded: MutableList<String> = ArrayList()

            for (p in permissions) {
                result = ContextCompat.checkSelfPermission(context, p)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p)
                }
            }

            // Check for permissions
            if (listPermissionsNeeded.isNotEmpty()) {
                (context as? Activity)?.let { activity ->
                    // Request permissions
                    ActivityCompat.requestPermissions(
                        activity,
                        listPermissionsNeeded.toTypedArray(),
                        SCAN_WIFI_REQUEST_CODE
                    )
                }
                return
            }
            //Permissions Already Granted
        }

        session?.startConnection()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            SCAN_WIFI_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    startSession()
                } else {
                    // Permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //remove the cyclic dependency
                    //checkAndStartScanWifi()
                }
            }
        }
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
     * There's an failure. See reason for explanation.
     */
    @Keep
    data class RequestConfigsError(val reason: Exception) : WiFiSessionStatus()

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

    /**
     * Сonnection by link is an failure. See reason for explanation.
     */
    @Keep
    data class ConnectionByLinkError(val reason: Exception) : WiFiSessionStatus()


    /**
     * Failed to create wifi config by current rule.
     */
    @Keep
    data class CreateWifiConfigError(val reason: Exception) : WiFiSessionStatus()

    /**
     *  Unsuccessful wifi point search
     */
    @Keep
    data class NotFoundWiFiPoint(val ssid: String?) : WiFiSessionStatus()

}