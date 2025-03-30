# Consumer ProGuard Rules for Analytics Module

# Additional rules for library consumption
-keep class com.thejawnpaul.gptinvestor.analytics.** { *; }

# Preserve all public methods in AnalyticsInterface
-keepclassmembers interface com.thejawnpaul.gptinvestor.analytics.Analytics {
    public *;
}