package se.umu.cs.c22jwt.garagelog.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import se.umu.cs.c22jwt.garagelog.data.Vehicle

/**
 * Data access object for the vehicle table.
 *
 * @see VehicleRepository
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE registration_number=(:registrationNumber)")
    fun getVehicle(registrationNumber: String): Flow<Vehicle?>

    @Query("SELECT EXISTS (SELECT 1 FROM vehicles WHERE registration_number=(:registrationNumber))")
    suspend fun exists(registrationNumber: String): Boolean

    @Insert
    suspend fun insert(vehicle: Vehicle)

    @Update
    suspend fun update(vehicle: Vehicle)

    @Query("DELETE FROM vehicles WHERE registration_number=(:registrationNumber)")
    suspend fun delete(registrationNumber: String)
}