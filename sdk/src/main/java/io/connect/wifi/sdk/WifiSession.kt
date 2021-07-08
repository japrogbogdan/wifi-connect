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
        val data = SessionData(apiKey, channelId, projectId, userId)
        val dump = DeviceDump(context).getDataDump()
        session = SessionExecutor(context, data, dump, callback)
        session?.start()
    }

    fun cancelSession() {
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
    object RequestConfigs : WiFiSessionStatus()
    object Connecting : WiFiSessionStatus()
    object Success : WiFiSessionStatus()
    data class Error(val reason: Exception) : WiFiSessionStatus()
    object CancelSession : WiFiSessionStatus()
}