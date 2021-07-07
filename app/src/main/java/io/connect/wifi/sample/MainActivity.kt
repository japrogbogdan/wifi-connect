package io.connect.wifi.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.connect.wifi.sample.cache.LocalCache
import io.connect.wifi.sample.databinding.ActivityMainBinding
import io.connect.wifi.sdk.WifiConnectionCommander
import io.connect.wifi.sdk.WifiRule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var commander: WifiConnectionCommander? = null
    private val cache: LocalCache by lazy { LocalCache(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputSsid.apply {
            setHint("ssid")
            setText(cache.ssid)
        }
        binding.inputPassword.apply {
            setHint("password")
            setText(cache.userPass)
        }
        binding.btContinue.setOnClickListener {
            doConnect()
        }
        commander = WifiConnectionCommander(activity = this)
    }

    private fun doConnect(){
        val ssid = binding.inputSsid.getText()
        val pass = binding.inputPassword.getText()

        if (ssid.isNotEmpty() && pass.isNotEmpty()){
            val rule = WifiRule.Builder()
                .ssid(ssid)
                .password(pass)
                .build()
            commander?.connectByRule(rule)
        }
    }

    override fun onStop() {
        super.onStop()
        cache.ssid = binding.inputSsid.getText()
        cache.userPass = binding.inputPassword.getText()
    }

    override fun onDestroy() {
        commander?.closeConnection()
        commander = null
        super.onDestroy()
    }
}