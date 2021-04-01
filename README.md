# Android Wifi Connect library

<p float="left" align="left">
    <img src="/images/sample.jpg" width="25%" />
</p>

## Steps to integrate sdk into your build:

Step 1. Add the JitPack repository to your build file

   Add it in your root build.gradle at the end of repositories:

  ```
  
  allprojects {
      repositories {
        ...
        maven {
        url 'https://jitpack.io'
        credentials {
            username 'obolsh'
            password 'jp_ksphc8r6itpht8gvhdekkl5hrs'
            }
        }
      }
    }
  ```

Jitpack [private repos documentation](https://jitpack.io/docs/PRIVATE/)

Step 2. Add the dependency

```
dependencies {
    ...
    implementation 'com.github.obolsh:wifi-connect:1.0.4'
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


## Steps to use sdk as standalone aar file:

Step 1: Build sdk-release.aar file

Execute in commandline:

```
./gradlew sdk:addMySourcesToAar
```

Step 2: Copy/paste sdk-release.aar file to your dependencies directory:

Copy sdk file from sdk/build/sdk-release.aar

Paste to your direcrory (Let's say 'libs')

Step 3: Add sdk aar as dependency:

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

Step 4. Add source of sdk:

In code click on one of sdk's reference classes

From toolbar press 'Press Navigate' -> 'Declaration or Usage'

On toolbar of opened class press 'Choose sources...' button

Inside popup select 'libs/sdk-sources.jar'. This file is inside .aar file

Reopen the same class.



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
