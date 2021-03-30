package io.connect.wifi.sdk

/**
 * Rule that we use to connect to wifi
 * @see io.connect.wifi.sdk.WifiRule.Builder to create new rule
 *
 * @since 1.0.1
 */
class WifiRule private constructor(

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.ssid
     */
    val ssid: String?,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.password
     */
    val password: String?,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.networkEncryption
     */
    val networkEncryption: NetworkEncryption?,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.hidden
     */
    val hidden: Boolean,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.identity
     */
    val identity: String?,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.anonymousIdentity
     */
    val anonymousIdentity: String?,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.eapMethod
     */
    val eapMethod: EapMethod?,

    /**
     * @see io.connect.wifi.sdk.WifiRule.Builder.phase2Method
     */
    val phase2Method: Phase2Method?
) {
    /**
     * Default WifiRule builder. This is the only option to create new rule.
     * @see io.connect.wifi.sdk.WifiRule
     */
    data class Builder(
        /**
         * set wifi point name. It can't be empty
         * This is mandatory value to be set.
         */
        var ssid: String? = null,

        /**
         * set password that we should use to connect to wifi point. It can't be empty
         * This is mandatory value to be set.
         */
        var password: String? = null,

        /**
         * set security type of wifi point.
         * @see io.connect.wifi.sdk.NetworkEncryption for supported types
         * Default value is [io.connect.wifi.sdk.NetworkEncryption.WPA2] if not set
         */
        var networkEncryption: NetworkEncryption = NetworkEncryption.WPA2,

        /**
         * set wifi point name visibility type
         * Default value is false.
         */
        var hidden: Boolean = false,

        /**
         * set identity for WifiEnterpriseConfig
         * @see android.net.wifi.WifiEnterpriseConfig
         */
        var identity: String? = null,

        /**
         * set anonymous identity. This is used as the unencrypted identity with
         * certain EAP types in WifiEnterpriseConfig
         * @see android.net.wifi.WifiEnterpriseConfig
         */
        var anonymousIdentity: String? = null,

        /**
         * set EAP authentication method for WifiEnterpriseConfig
         * @see io.connect.wifi.sdk.EapMethod
         * @see android.net.wifi.WifiEnterpriseConfig
         */
        var eapMethod: EapMethod? = null,

        /**
         * set Phase 2 authentication method. in WifiEnterpriseConfig
         * @see io.connect.wifi.sdk.Phase2Method
         * @see android.net.wifi.WifiEnterpriseConfig
         */
        var phase2Method: Phase2Method? = null
    ) {
        /**
         * set wifi point name. It can't be empty
         * This is mandatory value to be set.
         *
         * @param ssid - not empty name of wifi point
         */
        fun ssid(ssid: String) = apply { this.ssid = ssid }

        /**
         * set password that we should use to connect to wifi point. It can't be empty
         * This is mandatory value to be set.
         *
         * @param password - not empty password
         */
        fun password(password: String) = apply { this.password = password }

        /**
         * set security type of wifi point.
         * @see io.connect.wifi.sdk.NetworkEncryption for supported types
         * Default value is [io.connect.wifi.sdk.NetworkEncryption.WPA2] if not set
         *
         * @param networkEncryption - instance of NetworkEncryption
         */
        fun networkEncryption(networkEncryption: NetworkEncryption) =
            apply { this.networkEncryption = networkEncryption }

        /**
         * set wifi point name visibility type
         * Default value is false.
         *
         * @param hidden - true if wifi point is global visible, false if hidden
         */
        fun hidden(hidden: Boolean) = apply { this.hidden = hidden }

        /**
         * set identity for WifiEnterpriseConfig
         * @see android.net.wifi.WifiEnterpriseConfig
         *
         * @param identity - non empty value
         */
        fun identity(identity: String) = apply { this.identity = identity }

        /**
         * set anonymous identity. This is used as the unencrypted identity with
         * certain EAP types in WifiEnterpriseConfig
         * @see android.net.wifi.WifiEnterpriseConfig
         *
         * @param anonymousIdentity - non empty value
         */
        fun anonymousIdentity(anonymousIdentity: String) =
            apply { this.anonymousIdentity = anonymousIdentity }

        /**
         * set EAP authentication method for WifiEnterpriseConfig
         * @see io.connect.wifi.sdk.EapMethod
         * @see android.net.wifi.WifiEnterpriseConfig
         *
         * @param eapMethod - instance of EapMethod
         */
        fun eapMethod(eapMethod: EapMethod) = apply { this.eapMethod = eapMethod }

        /**
         * set Phase 2 authentication method. in WifiEnterpriseConfig
         * @see io.connect.wifi.sdk.Phase2Method
         * @see android.net.wifi.WifiEnterpriseConfig
         *
         * @param phase2Method - instance of Phase2Method
         */
        fun phase2Method(phase2Method: Phase2Method) = apply { this.phase2Method = phase2Method }

        /**
         * Finish building WiFiRule by creating it's new instance
         * @see io.connect.wifi.sdk.WifiRule
         * @return - new instance of WifiRule
         */
        fun build() = WifiRule(
            ssid,
            password,
            networkEncryption,
            hidden,
            identity,
            anonymousIdentity,
            eapMethod,
            phase2Method
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WifiRule

        if (ssid != other.ssid) return false
        if (password != other.password) return false
        if (networkEncryption != other.networkEncryption) return false
        if (hidden != other.hidden) return false
        if (identity != other.identity) return false
        if (anonymousIdentity != other.anonymousIdentity) return false
        if (eapMethod != other.eapMethod) return false
        if (phase2Method != other.phase2Method) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ssid?.hashCode() ?: 0
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (networkEncryption?.hashCode() ?: 0)
        result = 31 * result + hidden.hashCode()
        result = 31 * result + (identity?.hashCode() ?: 0)
        result = 31 * result + (anonymousIdentity?.hashCode() ?: 0)
        result = 31 * result + (eapMethod?.hashCode() ?: 0)
        result = 31 * result + (phase2Method?.hashCode() ?: 0)
        return result
    }


}