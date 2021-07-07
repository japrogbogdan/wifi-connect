package io.connect.wifi.sdk.cerificate

import java.security.cert.X509Certificate

interface CertificateFactory {

    fun createCertificate(raw: String): X509Certificate?
}