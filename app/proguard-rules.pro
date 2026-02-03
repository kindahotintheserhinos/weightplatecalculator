# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools proguard-defaults.txt

# Keep data classes for Gson serialization
-keepclassmembers class com.weightplatecalculator.data.model.** {
    *;
}

# Keep Compose related classes
-keep class androidx.compose.** { *; }

# General Android rules
-keepattributes Signature
-keepattributes *Annotation*
