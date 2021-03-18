package io.connect.wifi.sdk

sealed class NetworkEncryption {
    object WEP : NetworkEncryption()
    object WPA2 : NetworkEncryption()
    object WPA2EAP : NetworkEncryption()
    object NO_PASSWORD : NetworkEncryption()
}