# Android Wifi Connect library


## Steps to integrate sdk into your build:

Step 1. Add the JitPack repository to your build file

   Add it in your root build.gradle at the end of repositories:

  ```
  
  allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
  ```



Step 2. Add the dependency

```
dependencies {
    ...
    implementation 'com.github.obolsh:wifi-connect:1.0.1'
}
```

## Steps to connect to wifi:

Step 1. Build your wifi configuration:

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
