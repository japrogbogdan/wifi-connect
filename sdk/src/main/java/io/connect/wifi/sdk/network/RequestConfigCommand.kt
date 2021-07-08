package io.connect.wifi.sdk.network

import io.connect.wifi.sdk.data.SessionData
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.StringBuilder
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

internal class RequestConfigCommand(private val sessionData: SessionData) : NetworkCommand {

    companion object {
        private const val URL_LINK =
            "https://api.smartregion.online/project/%d/channel/%d/get_wifi_settings"
    }

    override fun sendRequest(body: String?): String? {
        println("Send request with body: $body")
        var connection: HttpsURLConnection? = null
        var output: OutputStream? = null
        var inputStream: InputStream? = null
        return try {
            val url =
                URL(
                    String.format(
                        Locale.US,
                        URL_LINK,
                        sessionData.projectId,
                        sessionData.channelId
                    )
                )
            connection = url.openConnection() as HttpsURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("X-API-KEY", sessionData.apiKey)
                setRequestProperty("Accept", "application/json")
                doOutput = true
            }

            body?.let {
                output = connection.outputStream
                val input = it.toByteArray()
                output?.write(input, 0, input.size)
            }

            inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))

            val response = StringBuilder()
            var responseLine: String? = reader.readLine()
            while (responseLine != null) {
                response.append(responseLine.trim())
                responseLine = reader.readLine()
            }
            return response.toString()
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
            output?.close()
            inputStream?.close()
        }
    }
}