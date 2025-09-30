package se.umu.cs.c22jwt.garagelog.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import se.umu.cs.c22jwt.garagelog.data.Service
import se.umu.cs.c22jwt.garagelog.data.Reminder
import se.umu.cs.c22jwt.garagelog.data.Vehicle

/**
 * Room database.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Database(
    version = 1,
    entities = [Vehicle::class, Service::class, Reminder::class],
    exportSchema = false
)
@TypeConverters(VehicleTypeConverters::class)
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceDao(): ServiceDao
    abstract fun reminderDao(): ReminderDao
}