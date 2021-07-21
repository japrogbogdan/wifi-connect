# Android Wifi Connect library (Sample for manual connection)

<p float="left" align="left">
    <img src="/images/sample.jpg" width="25%" />
</p>

## Steps to add sdk to your project:

Step 1: Copy/paste sdk-release.aar file to your dependencies directory(Let's say 'libs')

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


## Steps to connect to wifi by your SSID & password:

Step 1. Create new wifi configuration:

```
val rule = WifiRule.Builder()
        .ssid(ssid)
        .password(pass)
        .build()
```

Step 2. Create WifiConnectionCommander instance:

```
val commander = WifiConnectionCommander(activity = this)
```

Step 3. Start connection to wifi by sending your wifi configuration to commander:

```
commander.connectByRule(rule)
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
