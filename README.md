# Android Wifi Connect library

[Demo app for manual connection to wifi by your SSID & password](https://github.com/obolsh/wifi-connect/tree/main/app)

[Demo app for automatic connection to wifi](https://github.com/obolsh/wifi-connect/tree/main/demo)

[How to build sdk-release.aar file](https://github.com/obolsh/wifi-connect/tree/main/sdk)


## Steps to add sdk to your project:

Step 1: Copy/paste sdk-release.aar file to your dependencies directory (Let's say 'libs'):

Step 2: Add sdk aar as dependency:

```
allprojects {
    repositories {
       ...
        flatDir {
            dirs 'libs'
        }
    }
}
```

```
dependencies {
    ...
    implementation (name: 'sdk-release', ext: 'aar')
}
```

Step 3. (Optional) Add source of sdk:

In code click on one of sdk's reference classes

From toolbar press 'Navigate' -> 'Declaration or Usage'

On toolbar of opened class press 'Choose sources...' button

Inside popup select 'libs/sdk-sources.jar'. This file is inside .aar file

Reopen the same class.


## Steps to connect to wifi:

Step1. Check On WiFiModule

    /**
     * Сheck if Wi-Fi is enabled
     */
    fun isWifiEnabled(context: Context): Boolean

```

Step 2. Create wifi session instance:

```
private var wifi: WifiSession? = null

    /**
     * Create new session instance
     */
    private fun createSession(){
        val apiKey: String = "YOUR_API_KEY"
        val userId: String = "USER_ID"
        val apiDomain: String = "API_DOMAIN" //identifier for domain name server of the API SmartWiFi
        val channelId: Int = 1 //your channel id
        val projectId: Int = 1 //your project id
        val triggerSuccessTracking: Boolean = true //internally trigger success tracking url by sdk

        val contextReference: Context = this@MainActivity

        //Holds activity reference to call activity.startActivityForResult(Intent, Int) function when needed.
        //Will clear reference when session canceled.
        //This is required if contextReference is not activity reference.
        val activityHelper: ActivityHelper = ActivityHelperDelegate(activity = this@MainActivity)

        wifi = WifiSession.Builder(context = contextReference)
            .apiKey(apiKey)
            .userId(userId)
            .apiDomain(apiDomain)
            .channelId(channelId)
            .projectId(projectId)
            .autoDeliverSuccessCallback(triggerSuccessTracking)
            .activityHelper(activityHelper)
            .statusCallback(object : WifiSessionCallback {
                override fun onStatusChanged(newStatus: WiFiSessionStatus) {
                    when(newStatus){
                        WiFiSessionStatus.RequestConfigs -> { }

                        WiFiSessionStatus.ReceivedConfigs -> { }

                        is WiFiSessionStatus.RequestConfigsError -> {
                            val reason = newStatus.reason.message
                        }
                        is WiFiSessionStatus.CreateWifiConfigError -> {
                            val reason = newStatus.reason.message
                        }

                        WiFiSessionStatus.Connecting -> { }

                        WiFiSessionStatus.Success -> { }

                        is WiFiSessionStatus.ConnectionByLinkSend -> {
                            val link = newStatus.url
                        }

                        is WiFiSessionStatus.ConnectionByLinkSuccess -> {
                            val response = newStatus.response
                        }

                        is WiFiSessionStatus.ConnectionByLinkError -> {
                            val reason = newStatus.reason.message
                        }

                        is WiFiSessionStatus.NotFoundWiFiPoint -> {
                            val ssid = newStatus.ssid
                        }

                        is WiFiSessionStatus.Error -> {
                            //check the reason
                            newStatus.reason.printStackTrace()
                        }

                        WiFiSessionStatus.CancelSession -> {}
                    }
                }
            })
            .create()
    }
```

Step 3. Request permissions result for wifi scan:

```

    /**
     * Permissions result sent from activity into wifi session instance
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        wifi?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
```

Step 4. Request config and save to cache:

```
    /**
     * Get config if session instance present
     */
    private fun getSessionConfig(){
        wifi?.getSessionConfig()
    }

```

Step 5. Start wifi session (connect to wifi from cached config ):

```
    /**
     * Start session if session instance present
     */
    private fun startSession(){
        wifi?.startSession()
    }

```

Step 6. Clean session reference when navigate to other context (activity, fragment):

```
    /**
     * Cancel session when leaving current context(activity, fragment)
     * and clean reference to prevent leaks
     */
    private fun stopSession(){
        wifi?.let {
            it.cancelSession()
            wifi = null
        }
    }
```


## License

```Copyright 2021 Oleksii Bolshakov

Licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
