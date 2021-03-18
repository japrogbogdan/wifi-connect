package io.connect.wifi.sdk

sealed class EapMethod(val id: Int) {

    object NONE : EapMethod(-1)
    object PEAP : EapMethod(0)
    object TLS : EapMethod(1)
    object TTLS : EapMethod(2)
    object PWD : EapMethod(3)
    object SIM : EapMethod(4)
    object AKA : EapMethod(5)
    object AKA_PRIME : EapMethod(6)
    object UNAUTH_TLS : EapMethod(7)
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