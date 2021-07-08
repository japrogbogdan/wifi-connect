package io.connect.wifi.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.connect.wifi.sdk.WiFiSessionStatus
import io.connect.wifi.sdk.WifiSession
import io.connect.wifi.sdk.WifiSessionCallback

class MainActivity : AppCompatActivity() {

    private lateinit var wifi: WifiSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifi = WifiSession.Builder(this)
            .apiKey("pntr0-dhjopjl43-jH5nfvOmmljbdf")
            .userId("pntr54355430-dfbdb43-43t34mmljbdf")
            .channelId(1)
            .projectId(1)
            .statusCallback(object : WifiSessionCallback {
                override fun onStatusChanged(newStatus: WiFiSessionStatus) {
                    println("Wifi status changed: $newStatus")
                }
            })
            .create()
    }

    override fun onStart() {
        super.onStart()
        wifi.startSession()
    }

    override fun onDestroy() {
        wifi.cancelSession()
        super.onDestroy()
    }
}