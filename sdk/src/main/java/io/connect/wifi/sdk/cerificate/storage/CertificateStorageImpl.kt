package io.connect.wifi.sdk.cerificate.storage

import io.connect.wifi.sdk.cerificate.CertificateStorage
import java.security.cert.X509Certificate

class CertificateStorageImpl : CertificateStorage {

    private val cache = HashMap<String, X509Certificate>()

    override fun cacheCertificate(key: String, cert: X509Certificate) {
        cache[key] = cert
    }

    override fun getCachedCertificate(key: String): X509Certificate? {
        return cache[key]
    }
}