package io.connect.wifi.sdk.connect.delegate

import android.net.wifi.WifiManager
import android.net.wifi.hotspot2.PasspointConfiguration
import android.net.wifi.hotspot2.pps.Credential
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.cerificate.CertificateFactory
import io.connect.wifi.sdk.config.WifiConfig
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.O)
internal class PasspointDelegate(
    private val wifiManager: WifiManager,
    private val rule: WifiConfig.PasspointConfiguration,
    private val certificateFactory: CertificateFactory,
    private val status: (ConnectStatus) -> Unit
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

            wifiManager.addOrUpdatePasspointConfiguration(configuration)

            status.invoke(ConnectStatus.Success)
        } ?: status.invoke(ConnectStatus.Error(Exception("Missing certificate")))
    }

    override fun toString(): String {
        return "PasspointDelegate(rule=$rule)"
    }


}