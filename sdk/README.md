# Android Wifi Connect SDK

## Steps to build sdk as standalone aar file without encryption and with source code included:

Step 1: Use commandline and run:

```
./gradlew sdk:addMySourcesToAar
```

Step 2: Copy/paste sdk file from sdk/build/sdk-debug-{SDK_VERSION}.aar to your directory


## Steps to build sdk as standalone aar file:

Step 1: Use commandline and run:

```
./gradlew sdk:buildEncryptedSdk
```

Step 2: Copy/paste sdk file from sdk/build/sdk-release-{SDK_VERSION}.aar to your directory

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
