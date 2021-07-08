package io.connect.wifi.sdk.cerificate

import java.security.cert.X509Certificate

internal interface CertificateFactory {

    fun createCertificate(raw: String): X509Certificate?
}