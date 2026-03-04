-keepattributes *Annotation*
-keep class com.geofencetrigger.data.local.entity.** { *; }
-keep class com.geofencetrigger.data.remote.dto.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
