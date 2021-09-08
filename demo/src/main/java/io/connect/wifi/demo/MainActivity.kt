package io.connect.wifi.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.connect.wifi.demo.databinding.ActivityMainBinding
import io.connect.wifi.sdk.WiFiSessionStatus
import io.connect.wifi.sdk.WifiSession
import io.connect.wifi.sdk.WifiSessionCallback
import ui.helper.LocalCache
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cache: LocalCache by lazy { LocalCache(this) }

    private val API_KEY =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJwaWQiOiIxIiwic3ViIjoiZGM4ZDI4NGUtOWU3Mi00NmExLTk5YTctNDRhZmM2NjAzZjk5IiwiaWF0IjoxNjIxODc4MjY3LCJzY29wZXMiOlsicmVnaXN0cmF0aW9uX2dldF93aWZpX3NldHRpbmdzIl19.yfWqqg_zg_TjH0tyIWkU_8agcSSemCDOYBA4bqApOhi8Ygji5lC5Yf3-tU2kt-zT"

    private var wifi: WifiSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputToken.apply {
            setHint("x-api-key")

            var token = cache.apiToken
            if (token.isEmpty()) token = API_KEY

            setText(token)
        }
        binding.inputUserId.apply {
            setHint("user id")

            var id = cache.userId
            if (id.isEmpty()) id = "pntr54355430-dfbdb43-43t34mmljbdf"
            setText(id)
        }
        binding.inputApiDomain.apply {
            setHint("api domain")

            var domain = cache.apiDomain
            if (domain.isEmpty()) domain = "https://api.smartregion.online"
            setText(domain)
        }
        binding.inputProjectId.apply {
            setHint("project id")
            setText(cache.projectId)
        }
        binding.inputChannelId.apply {
            setHint("channel id")
            setText(cache.channelId)
        }
        binding.btContinue.setOnClickListener {
            doConnect()
        }
    }

    private fun doConnect() {
        try {
            val channel = binding.inputChannelId.getText().toInt()
            val project = binding.inputProjectId.getText().toInt()

            //удаляем сессию, если уже есть такая
            cleanSession()

            wifi = WifiSession.Builder(context = this)
                .apiKey(binding.inputToken.getText())
                .userId(binding.inputUserId.getText())
                .apiDomain(binding.inputApiDomain.getText())
                .channelId(channel)
                .projectId(project)
                .autoDeliverSuccessCallback(true)
                .statusCallback(object : WifiSessionCallback {
                    override fun onStatusChanged(newStatus: WiFiSessionStatus) {
                        binding.tvStatus.text =
                            resources.getString(R.string.connect_status, newStatus)
                    }
                })
                .create()
            wifi?.startSession()
        } catch (e: Throwable) {
            binding.tvStatus.text = resources.getString(
                R.string.connect_status, WiFiSessionStatus.Error(
                    Exception(e)
                )
            )
        }
    }

    override fun onStop() {
        super.onStop()
        cache.apiToken = binding.inputToken.getText()
        cache.userId = binding.inputUserId.getText()
        cache.apiDomain = binding.inputApiDomain.getText()
        cache.channelId = binding.inputChannelId.getText()
        cache.projectId = binding.inputProjectId.getText()
    }

    override fun onDestroy() {
        cleanSession()
        super.onDestroy()
    }

    private fun cleanSession(){
        wifi?.let {
            it.cancelSession()
            wifi = null
        }
    }
}