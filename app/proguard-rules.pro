# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/willi/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Signature,*Annotation*,SourceFile,LineNumberTable

-keep class com.grarak.kerneladiutor.** { *; }

-keep class com.bumptech.glide.** { *; }

-keep class com.google.android.apps.dashclock.** { *; }

-keep public class com.mattprecious.swirl.** { *; }

-keep public class org.adw.library.widgets.discreteseekbar.** { *; }

-keep class com.bvalosek.cpuspy.** { *; }
