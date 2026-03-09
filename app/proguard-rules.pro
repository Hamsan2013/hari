# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard config.

# Keep WebAppInterface methods accessible from JavaScript
-keepclassmembers class com.example.myapp.en.WebAppInterface {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }
