package io.connect.wifi.sdk.cerificate

import java.security.cert.X509Certificate

interface CertificateStorage {

    fun cacheCertificate(key: String, cert: X509Certificate)

    fun getCachedCertificate(key: String): X509Certificate?
}