package io.connect.wifi.sdk.connect.delegate

import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.cerificate.CertificateFactory
import io.connect.wifi.sdk.config.WifiConfig
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.Q)
internal class EnterpriseSuggestionDelegate(
    private val wifiManager: WifiManager,
    private val rule: WifiConfig.EnterpriseSuggestionConfiguration,
    private val certificateFactory: CertificateFactory,
    private val status: (ConnectStatus) -> Unit
) : ConnectionDelegate {

    private val suggestions = ArrayList<WifiNetworkSuggestion> ()

    private val configuration = WifiEnterpriseConfig().apply {
        eapMethod = WifiEnterpriseConfig.Eap.TTLS
        phase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2
        identity = rule.identity
        password = rule.password
    }

    override fun prepareDelegate() {

    }

    override fun connect() {
        status.invoke(ConnectStatus.Processing)
        certificateFactory.createCertificate(rule.caCertificate)?.let { cert ->
            configuration.caCertificate = cert
            val suggestion: WifiNetworkSuggestion = WifiNetworkSuggestion.Builder()
                .setWpa2EnterpriseConfig(configuration)
                .setSsid(rule.ssid)
                .build()

            suggestions.add(suggestion)

            wifiManager.removeNetworkSuggestions(suggestions)
            val status = wifiManager.addNetworkSuggestions(suggestions)
            readStatus(status)
        }  ?: status.invoke(ConnectStatus.Error(Exception("Missing certificate")))
    }

    private fun readStatus(statusCode: Int) {
        when (statusCode) {
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_INVALID")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED")))
            }
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED -> {
                status.invoke(ConnectStatus.Error(Exception("NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED")))
            }

            WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS -> {
                status.invoke(ConnectStatus.Success)
            }

            else -> {
                status.invoke(ConnectStatus.Error(Exception("Unknown status: $statusCode")))
            }

        }
    }

    override fun toString(): String {
        return "EnterpriseSuggestionDelegate(rule=$rule)"
    }


}