# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class com.saxpenguin.umamusumefuturesight.data.remote.** {
    <init>(...);
}
-keepnames class kotlinx.serialization.json.** { *; }

# Hilt/Dagger
-keep class com.saxpenguin.umamusumefuturesight.FutureSightApp_HiltComponents { *; }
-keep class dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper$1 { *; }
-keep class dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Room
-keep class androidx.room.RoomMasterTable {
    public static java.lang.String createInsertQuery(java.lang.String);
}
