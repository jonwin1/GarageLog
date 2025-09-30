package se.umu.cs.c22jwt.garagelog.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import se.umu.cs.c22jwt.garagelog.data.Service
import java.util.UUID

/**
 * Data access object for the service table.
 *
 * @see VehicleRepository
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE registration_number=(:registrationNumber) ORDER BY date DESC")
    fun getServices(registrationNumber: String): Flow<List<Service>>

    @Query("SELECT * FROM services WHERE id=(:id)")
    fun getService(id: UUID): Flow<Service?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: Service)

    @Update
    suspend fun update(service: Service)

    @Query("DELETE FROM services WHERE id=(:id)")
    suspend fun delete(id: UUID)
}