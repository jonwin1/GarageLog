package se.umu.cs.c22jwt.garagelog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class representing a vehicle.
 *
 * @property registrationNumber Registration number of the vehicle.
 * @property name               Name of the vehicle.
 * @property notes              Notes about the vehicle.
 * @property mileage            Mileage of the vehicle in kilometers.
 * @property photoFileName      Name of a picture of the vehicle.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey @ColumnInfo(name = "registration_number") val registrationNumber: String,
    val name: String,
    val notes: String,
    val mileage: Int?,
    @ColumnInfo(name = "photo_file_name") val photoFileName: String? = null
)