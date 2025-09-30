package se.umu.cs.c22jwt.garagelog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Class representation of a reminder. Remind after a number of kilometers or
 * months, whichever comes first.
 *
 * @property id                 A unique id.
 * @property title              Title of the reminder.
 * @property registrationNumber Registration number of the vehicle, foreign key into the vehicle table.
 * @property mileage            Mileage at creation of reminder, in kilometers.
 * @property date               Date at creation of reminder.
 * @property mileageInterval    Number of kilometers to remind after.
 * @property dateInterval       Number of months to remind after.
 * @property notes              Other notes about the reminder.
 * @property repeat             Whether to automatically repeat the reminder after completion.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Entity(
    tableName = "reminders", foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = arrayOf("registration_number"),
        childColumns = arrayOf("registration_number"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Reminder(
    @PrimaryKey val id: UUID,
    val title: String,
    @ColumnInfo(name = "registration_number") val registrationNumber: String,
    val mileage: Int,
    val date: Date,
    val mileageInterval: Int,
    val dateInterval: Int,
    val notes: String,
    val repeat: Boolean,
)
