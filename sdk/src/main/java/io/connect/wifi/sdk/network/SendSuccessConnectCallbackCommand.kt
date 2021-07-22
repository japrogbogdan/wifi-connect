package io.connect.wifi.sdk.network

import io.connect.wifi.sdk.data.SessionData

/**
 * @suppress Internal api
 *
 * Send success connection command
 */
internal class SendSuccessConnectCallbackCommand(
    private val sessionData: SessionData,
    private val url: String
) : BaseGetNetworkCommand() {

    override fun getUrlLink() = url

    override fun getApiKey() = sessionData.apiKey
}