package io.connect.wifi.sdk

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Wrapper above [android.net.wifi.WifiEnterpriseConfig.Eap]
 * @see android.net.wifi.WifiEnterpriseConfig.Phase2
 *
 * @since 1.0.1
 */
@Serializable
sealed class Phase2Method(val id: Int) {
    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.NONE
     */
    @Keep
    object NONE : Phase2Method(0)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.PAP
     */
    @Keep
    object PAP : Phase2Method(1)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.MSCHAP
     */
    @Keep
    object MSCHAP : Phase2Method(2)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.MSCHAPV2
     */
    @Keep
    object MSCHAPV2 : Phase2Method(3)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.GTC
     */
    @Keep
    object GTC : Phase2Method(4)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.SIM
     */
    @Keep
    object SIM : Phase2Method(5)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.AKA
     */
    @Keep
    object AKA : Phase2Method(6)

    /**
     * @see android.net.wifi.WifiEnterpriseConfig.Phase2.AKA_PRIME
     */
    @Keep
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