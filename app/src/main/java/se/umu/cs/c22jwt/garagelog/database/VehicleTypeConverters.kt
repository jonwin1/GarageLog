package se.umu.cs.c22jwt.garagelog.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for the Room database.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
class VehicleTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}