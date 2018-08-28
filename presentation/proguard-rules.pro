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

-dontwarn sun.misc.**

-keep class com.google.maps.DirectionsApi
-keep class com.google.maps.GeoApiContext

-keep class com.google.maps.** { *; }

-keep class com.google.android.ims { *; }
-keep class com.google.android.ims.** { *; }

#-keep interface com.google.android.gms.maps.** { *; }

-dontwarn okio.DeflaterSink
-dontwarn okio.Okio
-dontwarn retrofit2.Platform$Java8

-dontwarn com.google.appengine.api.urlfetch.*

#-keep class kotlinx.coroutines.experimental.android.AndroidExceptionPreHandler { *; }
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
