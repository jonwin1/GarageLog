package se.umu.cs.c22jwt.garagelog.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import se.umu.cs.c22jwt.garagelog.data.Reminder
import java.util.UUID

/**
 * Data access object for the reminder table.
 *
 * @see VehicleRepository
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE registration_number=(:registrationNumber)")
    fun getReminders(registrationNumber: String): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id=(:id)")
    fun getReminder(id: UUID): Flow<Reminder?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id=(:id)")
    suspend fun delete(id: UUID)
}