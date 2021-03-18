package io.connect.wifi.sdk

sealed class Phase2Method(val id: Int) {

    object NONE : Phase2Method(0)
    object PAP : Phase2Method(1)
    object MSCHAP : Phase2Method(2)
    object MSCHAPV2 : Phase2Method(3)
    object GTC : Phase2Method(4)
    object SIM : Phase2Method(5)
    object AKA : Phase2Method(6)
    object AKA_PRIME : Phase2Method(7)

}