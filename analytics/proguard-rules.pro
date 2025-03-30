
# Firebase Analytics
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Mixpanel
-dontwarn com.mixpanel.**
-keep class com.mixpanel.** { *; }

# Kotlin Reflection
-keep class kotlin.reflect.jvm.internal.** { *; }

# Keep annotated methods and classes
-keepattributes *Annotation*

# Preserve all public and private methods within the AnalyticsInterface
-keepclassmembers class * implements com.thejawnpaul.gptinvestor.analytics.Analytics {
    public *;
    private *;
}

# Preserve Dagger/Hilt injection
# -keepnames class * extends androidx.startup.Initializer
-keep class dagger.** { *; }
-keep @dagger.Module class *
-keep @javax.inject.Singleton class *

# Keep all public and protected methods in any classes extending the analytics implementations
-keepclassmembers public class * extends com.thejawnpaul.gptinvestor.analytics.Analytics {
    public protected *;
}