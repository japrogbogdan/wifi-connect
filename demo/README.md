# Android Wifi Connect library (Sample for auto connection)

<p float="left" align="left">
    <img src="/images/demo.jpg" width="25%" />
</p>

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

Step 1. Create wifi session instance:

```
private var wifi: WifiSession? = null

    /**
     * Create new session instance
     */
    private fun createSession(){
        val apiKey: String = "YOUR_API_KEY"
        val userId: String = "USER_ID"
        val channelId: Int = 1 //your channel id
        val projectId: Int = 1 //your project id

        wifi = WifiSession.Builder(context = this)
            .apiKey(apiKey)
            .userId(userId)
            .channelId(channelId)
            .projectId(projectId)
            .statusCallback(object : WifiSessionCallback {
                override fun onStatusChanged(newStatus: WiFiSessionStatus) {
                    when(newStatus){
                        WiFiSessionStatus.RequestConfigs -> { }

                        WiFiSessionStatus.Connecting -> { }

                        WiFiSessionStatus.Success -> { }

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

Step 2. Start wifi session (request config & connect to wifi):

```
    /**
     * Start session if session instance present
     */
    private fun startSession(){
        wifi?.startSession()
    }
```

Step 3. Clean session reference when navigate to other context (activity, fragment):

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