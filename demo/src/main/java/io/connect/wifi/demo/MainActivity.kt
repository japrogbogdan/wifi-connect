package io.connect.wifi.demo


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.tardyon.smartwifiv2.models.SmartWiFi
import com.tardyon.smartwifiv2.models.SmartWiFiCallBack
//import com.tardyon.smartwifiv2.builders.SmartWiFiBuilder
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
            if (domain.isEmpty()) domain =
                "https://api.sweetlife.smartregion.moscow" //"https://api.smartregion.online"
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
        binding.btGetConfig.setOnClickListener {
            doGetConfig()
        }
        binding.btConnect.setOnClickListener {
            doConnect()
        }
        binding.btExecOldSdk.setOnClickListener {
            doExecOldSDK()
        }
    }

    private fun doExecOldSDK() {
        val channelID = binding.inputChannelId.getText().toLong()
        val projectID = binding.inputProjectId.getText().toLong()

        val smartWiFi = SmartWiFi.getBuilder()
            .setApiDomain(binding.inputApiDomain.getText())
            .setChannelID(channelID)
            .setProjectID(projectID)
            .setJWTToken(binding.inputToken.getText())
            .setUserID(binding.inputUserId.getText())
            .setToken(binding.inputToken.getText())
            .build()
        smartWiFi.execute(object : SmartWiFiCallBack {
            override fun success() {
                Toast.makeText(
                    this@MainActivity,
                    "old sdk exec success",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun error(error: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "old sdk exec error=${error.message.orEmpty()}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun infoMessage(message: String) {
                Toast.makeText(
                    this@MainActivity,
                    "old sdk exec msg=${message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        wifi?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initWifiSession() {
        try {
            val channel = binding.inputChannelId.getText().toInt()
            val project = binding.inputProjectId.getText().toInt()

            //удаляем сессию, если уже есть такая
            cleanSession()

            //val activityHelper: ActivityHelper = ActivityHelperDelegate(activity = this@MainActivity)

            wifi = WifiSession.Builder(context = this)
                .apiKey(binding.inputToken.getText())
                .userId(binding.inputUserId.getText())
                .apiDomain(binding.inputApiDomain.getText())
                .channelId(channel)
                .projectId(project)
                .autoDeliverSuccessCallback(true)
                //.activityHelper(activityHelper)
                .statusCallback(object : WifiSessionCallback {
                    override fun onStatusChanged(newStatus: WiFiSessionStatus) {
                        binding.tvStatus.text =
                            resources.getString(R.string.connect_status, newStatus)
                        when (newStatus) {
                            WiFiSessionStatus.RequestConfigs -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "RequestConfigs",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.ReceivedConfigs -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "ReceivedConfigs rules=${newStatus.rules} traceId=${newStatus.traseId}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.RequestConfigsError -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "RequestConfigs Error=${newStatus.reason.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.CreateWifiConfigError -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "CreateWifiConfig Error=${newStatus.reason.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            WiFiSessionStatus.Connecting -> {
                                Toast.makeText(this@MainActivity, "Connecting", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            WiFiSessionStatus.Success -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Connecting Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.ConnectionByLinkSend -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "ConnectionByLink Url=${newStatus.url}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.ConnectionByLinkSuccess -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "ConnectionByLinkSuccess respose=${newStatus.response}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.ConnectionByLinkError -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "ConnectionByLink Error=${newStatus.reason.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.NotFoundWiFiPoint -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Can\'t find wifi current ssid=${newStatus.ssid}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is WiFiSessionStatus.Error -> {
                                //check the reason
                                newStatus.reason.printStackTrace()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Connection Error=${newStatus.reason.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                })
                .create()
        } catch (e: Throwable) {
            binding.tvStatus.text = resources.getString(
                R.string.connect_status, WiFiSessionStatus.Error(
                    Exception(e)
                )
            )
        }
    }

    private fun showWiFiDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.alert_dialog_title)
            setMessage(R.string.alert_dialog_message)
            setPositiveButton(R.string.alert_dialog_buttton_yes, positiveButtonClick)
            setNegativeButton(R.string.alert_dialog_buttton_no, negativeButtonClick)
            create()
        }.show()
    }

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        this.startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS), 0)
    }

    private val negativeButtonClick = { dialog: DialogInterface, which: Int ->

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Save WiFi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Cancel Save WiFi", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun doGetConfig() {
        if (WifiSession.isWifiEnabled(this).not())
            showWiFiDialog()
        else
            try {
                initWifiSession()
                wifi?.getSessionConfig()
            } catch (e: Throwable) {
                binding.tvStatus.text = resources.getString(
                    R.string.connect_status, WiFiSessionStatus.Error(
                        Exception(e)
                    )
                )
            }
    }

    private fun doConnect() {
        if (WifiSession.isWifiEnabled(this).not())
            showWiFiDialog()
        else {
            if (wifi == null)
                initWifiSession()
            wifi?.startSession()
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

    private fun cleanSession() {
        wifi?.let {
            it.cancelSession()
            wifi = null
        }
    }
}