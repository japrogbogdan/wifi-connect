# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class io.connect.wifi.sdk.WifiSession { *; }
-keep class io.connect.wifi.sdk.WifiSessionCallback { *; }
-keep class io.connect.wifi.sdk.WiFiSessionStatus { *; }
-keep class io.connect.wifi.sdk.WifiRule { *; }
-keep class io.connect.wifi.sdk.WifiConnectionCommander { *; }
-keep class io.connect.wifi.sdk.Phase2Method { *; }
-keep class io.connect.wifi.sdk.NetworkEncryption { *; }
-keep class io.connect.wifi.sdk.EapMethod { *; }
-keep class io.connect.wifi.sdk.ConnectStatus { *; }
-keep class io.connect.wifi.sdk.activity.** { *; }

-keepclassmembers class io.connect.wifi.sdk.WifiSession {
    public static ** Companion;
}
-keep class io.connect.wifi.sdk.WifiSession$Companion { *; }

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

