# Add project specific ProGuard rules here.
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class com.dualspace.clone.model.** { *; }
