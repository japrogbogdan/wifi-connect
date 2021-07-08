package io.connect.wifi.sdk

/**
 * Wrapper above [android.net.wifi.WifiEnterpriseConfig.Eap]
 * @see android.net.wifi.WifiEnterpriseConfig.Phase2
 *
 * @since 1.0.1
 */
sealed class Phase2Method(val id: Int) {
    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.NONE
     */
    object NONE : Phase2Method(0)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.PAP
     */
    object PAP : Phase2Method(1)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.MSCHAP
     */
    object MSCHAP : Phase2Method(2)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.MSCHAPV2
     */
    object MSCHAPV2 : Phase2Method(3)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.GTC
     */
    object GTC : Phase2Method(4)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.SIM
     */
    object SIM : Phase2Method(5)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.AKA
     */
    object AKA : Phase2Method(6)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.AKA_PRIME
     */
    object AKA_PRIME : Phase2Method(7)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Phase2Method

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}