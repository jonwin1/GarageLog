package se.umu.cs.c22jwt.garagelog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Class representing a service entry for a specific vehicle.
 *
 * @property id                 A unique id.
 * @property title              Title of the service entry.
 * @property registrationNumber Registration number of the vehicle, foreign key into the vehicle table.
 * @property date               Date of the service.
 * @property mileage            Mileage at the service in kilometers.
 * @property cost               Cost of the service.
 * @property notes              Any other notes about the service.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Entity(
    tableName = "services", foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = arrayOf("registration_number"),
        childColumns = arrayOf("registration_number"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Service(
    @PrimaryKey val id: UUID,
    val title: String,
    @ColumnInfo(name = "registration_number", index = true) val registrationNumber: String,
    val date: Date,
    val mileage: Int,
    val cost: Int,
    val notes: String,
)
