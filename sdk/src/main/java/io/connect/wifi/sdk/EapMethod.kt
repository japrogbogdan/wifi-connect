package io.connect.wifi.sdk

/**
 * Wrapper above WifiEnterpriseConfig.Eap
 * @see android.net.wifi.WifiEnterpriseConfig.Eap
 *
 * @since 1.0.1
 */
internal sealed class EapMethod(val id: Int) {
    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.NONE
     */
    object NONE : EapMethod(-1)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.PEAP
     */
    object PEAP : EapMethod(0)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.TLS
     */
    object TLS : EapMethod(1)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.TTLS
     */
    object TTLS : EapMethod(2)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.PWD
     */
    object PWD : EapMethod(3)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.SIM
     */
    object SIM : EapMethod(4)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.AKA
     */
    object AKA : EapMethod(5)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.AKA_PRIME
     */
    object AKA_PRIME : EapMethod(6)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.UNAUTH_TLS
     */
    object UNAUTH_TLS : EapMethod(7)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Eap.WAPI_CERT
     */
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