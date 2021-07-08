package io.connect.wifi.sdk.cerificate.factory

import android.util.Base64
import io.connect.wifi.sdk.cerificate.CertificateFactory
import io.connect.wifi.sdk.cerificate.CertificateStorage
import java.io.ByteArrayInputStream
import java.security.cert.X509Certificate

internal class CertificateFactoryImpl(private val storage: CertificateStorage) : CertificateFactory {

    override fun createCertificate(raw: String): X509Certificate? {
        return storage.getCachedCertificate(raw)?.let {
            it
        } ?: loadCertificate(raw)?.also {
            saveCertificate(raw, it)
        }
    }

    private fun loadCertificate(raw: String): X509Certificate? {
        return try {
            val decoded = ByteArrayInputStream(Base64.encode(raw.toByteArray(), Base64.DEFAULT))
            val cert = java.security.cert.CertificateFactory.getInstance("X.509")
                .generateCertificate(decoded) as X509Certificate
            return cert.also {
                decoded.close()
            }
        } catch (e: Throwable) {
            null
        }
    }

    private fun saveCertificate(raw: String, cert: X509Certificate) {
        storage.cacheCertificate(raw, cert)
    }
}