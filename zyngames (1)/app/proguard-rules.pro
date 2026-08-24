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

# Preserve source file and line number information for Google Play Console deobfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin & Coroutines ProGuard rules
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Compose internal annotations and runtime classes
-keep class androidx.compose.runtime.** { *; }
