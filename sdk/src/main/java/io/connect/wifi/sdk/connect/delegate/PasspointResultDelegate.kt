package io.connect.wifi.sdk.connect.delegate

import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.net.wifi.hotspot2.pps.Credential
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.cerificate.CertificateFactory
import io.connect.wifi.sdk.config.WifiConfig
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.R)
internal class PasspointResultDelegate(
    private val rule: WifiConfig.PasspointResultConfiguration,
    private val certificateFactory: CertificateFactory,
    private val status: (ConnectStatus) -> Unit,
    private val startActivityForResult: (Intent, Int) -> Unit
) : ConnectionDelegate {


    private val configuration = PasspointConfiguration()

    private val credential = Credential().apply {
        realm = rule.realm
    }

    private val homeSp = HomeSp().apply {
        friendlyName = rule.friendlyName
        fqdn = rule.fqdn
    }

    private val userCredential = Credential.UserCredential().apply {
        eapType = rule.eapType.toInt()
        nonEapInnerMethod = rule.nonEapInnerMethod
        username = rule.username
        password = Base64.encodeToString(rule.password.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    override fun prepareDelegate() {

    }

    override fun connect() {
        status.invoke(ConnectStatus.Processing)
        certificateFactory.createCertificate(rule.caCertificate)?.let { cert ->
            credential.caCertificate = cert
            credential.userCredential = userCredential
            configuration.credential = credential
            configuration.homeSp = homeSp

            val session = WifiNetworkSuggestion.Builder()
                .setPriority(Int.MAX_VALUE)
                .setPasspointConfig(configuration)
                .build()

            val bundle = Bundle().apply {
                putParcelableArrayList(Settings.EXTRA_WIFI_NETWORK_LIST, arrayListOf(session))
            }
            val intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS).apply {
                putExtras(bundle)
            }
            startActivityForResult.invoke(intent, 0)

            status.invoke(ConnectStatus.Success)
        } ?: status.invoke(ConnectStatus.Error(Exception("Missing certificate")))
    }

    override fun toString(): String {
        return "PasspointResultDelegate(rule=$rule)"
    }


}