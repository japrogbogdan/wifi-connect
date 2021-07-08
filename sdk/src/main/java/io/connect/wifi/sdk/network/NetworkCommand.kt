package io.connect.wifi.sdk.network

internal interface NetworkCommand {

    fun sendRequest(body: String?): String?
}