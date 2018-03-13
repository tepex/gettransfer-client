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


-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.google.maps.DirectionsApi
-keep class com.google.maps.GeoApiContext

-keep class com.google.maps.** { *; }

-keep class com.google.android.ims { *; }
-keep class com.google.android.ims.** { *; }

#-keep interface com.google.android.gms.maps.** { *; }


-dontnote rx.internal.util.PlatformDependent

-dontwarn com.github.davidmoten.rx2.flowable.Serialized*
-dontwarn com.github.davidmoten.rx2.internal.flowable.buffertofile.MemoryMappedFile
-dontwarn com.github.davidmoten.rx2.internal.flowable.buffertofile.UnsafeAccess

-dontwarn okio.DeflaterSink
-dontwarn okio.Okio
-dontwarn retrofit2.Platform$Java8

-dontwarn com.google.appengine.api.urlfetch.*
-dontwarn org.joda.convert.*
-dontwarn org.slf4j.impl.*
