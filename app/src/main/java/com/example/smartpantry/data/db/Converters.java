package com.example.smartpantry.data.db;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Room TypeConverters for Date and Long timestamps.
 */
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
