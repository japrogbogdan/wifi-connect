package io.connect.wifi.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.config.WifiConfig
import io.connect.wifi.sdk.connect.ConnectionCommand
import io.connect.wifi.sdk.connect.ConnectionManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal object Controller {

    private val executorService: ExecutorService by lazy { Executors.newFixedThreadPool(4) }
    private val mainThreadHandler: Handler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }

    private val factory: WifiConfigFactory by lazy { WifiConfigFactory() }
    private var manager: ConnectionManager? = null
    private var command: ConnectionCommand? = null

    fun startConnection(context: Context, rule: WifiRule) {
        initParams(context)

        command?.let {
            it.wifiRule = rule
            executorService.execute(it)
        }
    }

    private fun initParams(context: Context) {
        if (manager == null)
            manager =
                ConnectionManager(context.getSystemService(Context.WIFI_SERVICE) as WifiManager) { intent, i ->
                    mainThreadHandler.post {
                        (context as? Activity)?.startActivityForResult(intent, i)
                    }
                }

        if (command == null) {
            command = ConnectionCommand(factory, manager)
        }
    }
}