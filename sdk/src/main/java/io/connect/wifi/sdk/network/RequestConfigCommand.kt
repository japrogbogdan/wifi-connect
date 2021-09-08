package io.connect.wifi.sdk.network

import io.connect.wifi.sdk.data.SessionData
import java.util.*

/**
 * @suppress Internal api
 *
 * Request wifi rules to use for connection to wifi
 */
internal class RequestConfigCommand(private val sessionData: SessionData) :
    BasePostNetworkCommand() {

    companion object {
        private const val URL_LINK = "%s/project/%d/channel/%d/get_wifi_settings"
    }

    override fun getUrlLink() =
        String.format(
            Locale.US, URL_LINK, sessionData.apiDomain, sessionData.projectId, sessionData.channelId
        )

    override fun getApiKey() = sessionData.apiKey
}