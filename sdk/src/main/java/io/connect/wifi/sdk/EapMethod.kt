package io.connect.wifi.sdk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Wrapper above WifiEnterpriseConfig.Eap
 * @see android.net.wifi.WifiEnterpriseConfig.Eap
 *
 * @since 1.0.1
 */
@Parcelize
//sealed
open class EapMethod(val id: Int): Parcelable {
    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.NONE
     */
    @Keep
    object NONE : EapMethod(-1)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.PEAP
     */
    @Keep
    object PEAP : EapMethod(0)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.TLS
     */
    @Keep
    object TLS : EapMethod(1)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.TTLS
     */
    @Keep
    object TTLS : EapMethod(2)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.PWD
     */
    @Keep
    object PWD : EapMethod(3)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.SIM
     */
    @Keep
    object SIM : EapMethod(4)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.AKA
     */
    @Keep
    object AKA : EapMethod(5)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.AKA_PRIME
     */
    @Keep
    object AKA_PRIME : EapMethod(6)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.UNAUTH_TLS
     */
    object UNAUTH_TLS : EapMethod(7)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.WAPI_CERT
     */
    @Keep
    object WAPI_CERT : EapMethod(8)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EapMethod

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}