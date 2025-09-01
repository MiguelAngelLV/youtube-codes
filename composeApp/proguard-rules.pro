# ProGuard rules for Compose Desktop app using Google API client
# Keep necessary metadata for reflection
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, AnnotationDefault

# Keep Gson classes and model types (use reflection)
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.** { *; }
-keep class com.google.gson.internal.** { *; }

# Keep Google API client classes which may use reflection
-keep class com.google.api.** { *; }
-keep class com.google.api.client.** { *; }
-keep class com.google.apis.** { *; }
-keep class com.google.auth.** { *; }
-keep class com.google.oauth.** { *; }
-keep class com.google.common.reflect.** { *; }

# Apache HTTP / Commons utils used by google-api-client
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.logging.** { *; }

# Suppress warnings for optional server-side/logging/appengine/grpc classes not present at runtime
-dontwarn javax.servlet.**
-dontwarn org.apache.avalon.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.commons.logging.LogSource
-dontwarn org.apache.log4j.**
-dontwarn org.apache.log.**
-dontwarn com.google.appengine.**
-dontwarn com.google.apphosting.**
-dontwarn io.grpc.override.**
-dontwarn io.opencensus.**
-dontwarn com.google.common.util.concurrent.MoreExecutors

# If HttpClient shaded classes emit notes, ignore
-dontwarn org.apache.http.**

# Don’t obfuscate our own code packages to simplify debugging (optional)
-keep class org.malv.youtube.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Compose runtime classes keep (usually safe)
-keep class androidx.compose.** { *; }

# Keep enums and data classes reflective names
-keepclassmembers enum * { *; }


# Additional suppressions to resolve unresolved references reported by ProGuard
-dontwarn kotlin.concurrent.atomics.**
-dontwarn kotlin.jvm.internal.EnhancedNullability
-dontwarn org.checkerframework.**
-dontwarn com.google.common.**
