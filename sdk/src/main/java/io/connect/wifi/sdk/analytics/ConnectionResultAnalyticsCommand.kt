package io.connect.wifi.sdk.analytics

import io.connect.wifi.sdk.data.DeviceData
import io.connect.wifi.sdk.data.SessionData
import io.connect.wifi.sdk.network.SendConnectionResultCommand

/**
 * @suppress Internal api
 *
 * Send analytics to internal api
 */
internal class ConnectionResultAnalyticsCommand(
    private val sessionData: SessionData,
    private val dump: DeviceData,
    private val data: List<ConnectResult>
) : AnalyticsCommand {

    override fun send() {
        val body = sessionData.toConnectionResultBody(dump, data)
        val request = SendConnectionResultCommand(sessionData)
        request.sendRequest(body)
    }
}