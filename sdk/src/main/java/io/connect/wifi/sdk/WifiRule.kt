package io.connect.wifi.sdk

class WifiRule private constructor(
    val ssid: String?,
    val password: String?,
    val networkEncryption: NetworkEncryption?,
    val hidden: Boolean,
    val identity: String?,
    val anonymousIdentity: String?,
    val eapMethod: EapMethod?,
    val phase2Method: Phase2Method?
) {

    data class Builder(
        var ssid: String? = null,
        var password: String? = null,
        var networkEncryption: NetworkEncryption = NetworkEncryption.WPA2,
        var hidden: Boolean = false,
        var identity: String? = null,
        var anonymousIdentity: String? = null,
        var eapMethod: EapMethod? = null,
        var phase2Method: Phase2Method? = null
    ) {

        fun ssid(ssid: String) = apply { this.ssid = ssid }
        fun password(password: String) = apply { this.password = password }
        fun networkEncryption(networkEncryption: NetworkEncryption) = apply { this.networkEncryption = networkEncryption }
        fun hidden(hidden: Boolean) = apply { this.hidden = hidden }
        fun identity(identity: String) = apply { this.identity = identity }
        fun anonymousIdentity(anonymousIdentity: String) = apply { this.anonymousIdentity = anonymousIdentity }
        fun eapMethod(eapMethod: EapMethod) = apply { this.eapMethod = eapMethod }
        fun phase2Method(phase2Method: Phase2Method) = apply { this.phase2Method = phase2Method }

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