package io.connect.wifi.sdk.connect.delegate

import android.content.Context
import android.net.*
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import io.connect.wifi.sdk.ConnectStatus
import io.connect.wifi.sdk.config.WifiConfig

/**
 * @suppress Internal api
 *
 * Delegate implementation for WifiConfig.Wpa2PassphraseSuggestion
 *
 * @see io.connect.wifi.sdk.config.WifiConfig.Wpa2PassphraseSuggestion
 *
 * @since 1.0.1
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class Wpa2Api29Delegate(
    private val connectivityManager: ConnectivityManager,
    private val rule: WifiConfig.Wpa2PassphraseSuggestion,
    private val status: (ConnectStatus) -> Unit
) : ConnectionDelegate {

    private var isRegister = false

    val networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                //phone is connected to wifi network

                // To make sure that requests don't go over mobile data
                connectivityManager.bindProcessToNetwork(network)

                status.invoke(ConnectStatus.Success)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                //phone is about to lose connection to network
                status.invoke(ConnectStatus.Error(Exception("phone is about to lose connection to network")))
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                //phone lost connection to network
                connectivityManager.bindProcessToNetwork(null)
                //connectivityManager.unregisterNetworkCallback(networkCallback)
                status.invoke(ConnectStatus.Error(Exception("phone lost connection to network")))
            }

            override fun onUnavailable() {
                super.onUnavailable()
                //user cancelled wifi connection
                status.invoke(ConnectStatus.Error(Exception("user cancelled wifi connection")))
            }
        }

    /**
     * Make delegate implementation available for connection
     */
    override fun prepareDelegate() {
        //Unregistering network callback instance supplied to requestNetwork call disconnects phone from the connected network
        if (isRegister)
            connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * Connect to wifi
     */
    override fun connect() {
        val networkSpecifier: NetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(rule.ssid) //for test "AndroidWifi"
            .setWpa2Passphrase(rule.password)
            //.setIsHiddenSsid(true) //specify if the network does not broadcast itself and OS must perform a forced scan in order to connect
            .build()
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(networkSpecifier)
            .build()

        connectivityManager.requestNetwork(networkRequest, networkCallback)
        isRegister = true
    }

    override fun toString(): String {
        return "Wpa2Api29Delegate(rule=$rule)"
    }


}