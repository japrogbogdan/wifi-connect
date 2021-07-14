package io.connect.wifi.sdk

import android.content.Context
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.session.SessionExecutor
import io.connect.wifi.sdk.util.DeviceDump
import java.lang.Exception

class WifiSession private constructor(
    val context: Context,
    val apiKey: String,
    val userId: String,
    val channelId: Int,
    val projectId: Int,
    val callback: WifiSessionCallback?
) {

    private var session: SessionExecutor? = null

    fun startSession() {
        val data = SessionData(apiKey, channelId, projectId, userId, null)
        val dump = DeviceDump(context).getDataDump()
        LogUtils.debug("[WifiSession] Start session:\ndata:$data\ndump:$dump")
        session = SessionExecutor(context, data, dump, callback)
        session?.start()
    }

    fun cancelSession() {
        LogUtils.debug("[WifiSession] Cancel session")
        session?.cancel()
    }

    data class Builder(
        val context: Context,
        var apiKey: String? = null,
        var userId: String? = null,
        var channelId: Int = 0,
        var projectId: Int = 0,
        var callback: WifiSessionCallback? = null
    ) {

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }

        fun userId(userId: String) = apply { this.userId = userId }

        fun channelId(channelId: Int) = apply { this.channelId = channelId }

        fun projectId(projectId: Int) = apply { this.projectId = projectId }

        fun statusCallback(callback: WifiSessionCallback) = apply { this.callback = callback }

        fun create() =
            WifiSession(context, apiKey.orEmpty(), userId.orEmpty(), channelId, projectId, callback)
    }
}

interface WifiSessionCallback {

    fun onStatusChanged(newStatus: WiFiSessionStatus)
}

sealed class WiFiSessionStatus {
    object RequestConfigs : WiFiSessionStatus() {
        override fun toString() = "RequestConfigs"
    }

    object Connecting : WiFiSessionStatus() {
        override fun toString() = "Connecting"
    }

    object Success : WiFiSessionStatus() {
        override fun toString() = "Success"
    }

    data class Error(val reason: Exception) : WiFiSessionStatus()

    object CancelSession : WiFiSessionStatus() {
        override fun toString() = "CancelSession"
    }
}