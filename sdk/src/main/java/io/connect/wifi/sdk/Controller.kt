package io.connect.wifi.sdk

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import io.connect.wifi.sdk.connect.ConnectionCommand
import io.connect.wifi.sdk.connect.ConnectionManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @suppress Internal api
 *
 * Singleton controller that will communicate with other SDK parts. This is an entry point of internal
 * sdk layer.
 *
 * @since 1.0.1
 */
internal object Controller {

    private val executorService: ExecutorService by lazy { Executors.newFixedThreadPool(4) }
    private val mainThreadHandler: Handler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }

    private val factory: WifiConfigFactory by lazy { WifiConfigFactory() }
    private var manager: ConnectionManager? = null
    private var command: ConnectionCommand? = null

    /**
     * Start connection by rules.
     * We'll proceed this request in background thread. However, we'r able to use main thread to
     * access {android.app.Activity} functions.
     *
     * @param context - provide {@link android.content.Context} to grant access to {@link android.net.wifi.WifiManager}
     * @param rule - provide {@link io.connect.wifi.sdk.WifiRule} to define how to connect to wifi
     *
     * @see io.connect.wifi.sdk.WifiRule
     */
    fun startConnection(context: Context, rule: WifiRule) {
        initParams(context)

        command?.let {
            it.wifiRule = rule
            executorService.execute(it)
        }
    }

    /**
     * Instantiate all references to be used later.
     */
    private fun initParams(context: Context) {
        if (manager == null)
            manager =
                ConnectionManager(context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager) { intent, i ->
                    mainThreadHandler.post {
                        (context as? Activity)?.startActivityForResult(intent, i)
                    }
                }

        if (command == null) {
            command = ConnectionCommand(factory, manager)
        }
    }
}