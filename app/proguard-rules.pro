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
# Room entities
# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class * extends androidx.lifecycle.ViewModel
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
# Gson
-keep class com.google.gson.** { *; }
-keep class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# Room
-keep class androidx.room.** { *; }
-keepclassmembers class * {
   @androidx.room.* <methods>;
}
# Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  static ** CREATOR;
}
-keep class com.drmiaji.prayertimes.** { *; }
-keep class com.drmiaji.prayertimes.service.** { *; }
