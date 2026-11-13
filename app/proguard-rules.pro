# Keep Android ViewModel constructor invoked via reflection
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Keep Room Database & generated _Impl classes
-keep class * extends androidx.room.RoomDatabase
-keep class com.example.data.local.** { *; }
-dontwarn androidx.room.**

# Keep Compose & Kotlin runtime essentials
-keepattributes *Annotation*
-dontwarn kotlin.annotations.experimental.**
