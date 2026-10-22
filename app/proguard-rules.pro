# Keep Room annotations and generated classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }

# Keep Gson models
-keep class com.example.smartpantry.data.seed.** { *; }
