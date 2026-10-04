# ProGuard configuration for FilterSaathi
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class org.filtersaathi.app.data.local.entity.** { *; }
