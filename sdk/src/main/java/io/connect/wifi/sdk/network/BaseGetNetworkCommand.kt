package io.connect.wifi.sdk.network

import io.connect.wifi.sdk.internal.LogUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal abstract class BaseGetNetworkCommand : NetworkCommand {

    internal abstract fun getUrlLink(): String

    internal abstract fun getApiKey(): String

    override fun sendRequest(body: String?): String? {
        var connection: HttpURLConnection? = null
        var output: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            val link = getUrlLink()
            val url = URL(link)
            LogUtils.debug("[BaseGetNetworkCommand] Begin request:\nurl: $url")

            connection = if (link.startsWith("https"))
                url.openConnection() as HttpsURLConnection
            else url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                setRequestProperty("x-api-key", getApiKey())
                setRequestProperty("Host", url.host)
                setRequestProperty("Accept", "application/json")
            }

            inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))

            val response = StringBuilder()
            var responseLine: String? = reader.readLine()
            while (responseLine != null) {
                response.append(responseLine.trim())
                responseLine = reader.readLine()
            }
            return response.toString().also {
                LogUtils.debug("[BaseGetNetworkCommand] Response received\n$it")
            }
        } catch (e: Throwable) {
            LogUtils.debug("[BaseGetNetworkCommand] Error", e)
            throw Exception(e)
        } finally {
            connection?.disconnect()
            output?.close()
            inputStream?.close()
        }
    }
}