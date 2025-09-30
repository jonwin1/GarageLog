package se.umu.cs.c22jwt.garagelog.database

import kotlinx.coroutines.flow.Flow
import se.umu.cs.c22jwt.garagelog.data.Service
import se.umu.cs.c22jwt.garagelog.data.Reminder
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for abstracted access to the database.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
class VehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val serviceDao: ServiceDao,
    private val reminderDao: ReminderDao,
) {
    // ========================================================================
    //                              Vehicles
    // ========================================================================

    /**
     * Get all vehicles.
     *
     * @return A flow with a list of vehicles.
     */
    fun getVehicles(): Flow<List<Vehicle>> = vehicleDao.getVehicles()

    /**
     * Get a specific vehicle.
     *
     * @param registrationNumber    Registration number of the vehicle.
     * @return                      A flow with a vehicle.
     */
    fun getVehicle(registrationNumber: String): Flow<Vehicle?> =
        vehicleDao.getVehicle(registrationNumber)

    /**
     * Check if a vehicle with the specified registration number already exists.
     *
     * @param registrationNumber    The registration number to check.
     * @return                      True if it is present, false otherwise.
     */
    suspend fun exists(registrationNumber: String): Boolean {
        return vehicleDao.exists(registrationNumber)
    }

    /**
     * Insert a vehicle into the database.
     *
     * @param vehicle   The vehicle to insert.
     */
    suspend fun insert(vehicle: Vehicle) {
        vehicleDao.insert(vehicle)
    }

    /**
     * Update a vehicle in the database.
     *
     * @param vehicle   The updated vehicle.
     */
    suspend fun update(vehicle: Vehicle) {
        vehicleDao.update(vehicle)
    }

    /**
     * Delete a vehicle from the database.
     *
     * @param registrationNumber    Registration number of the vehicle to delete.
     */
    suspend fun deleteVehicle(registrationNumber: String) {
        vehicleDao.delete(registrationNumber)
    }

    // ========================================================================
    //                              Services
    // ========================================================================

    /**
     * Get all services for a vehicle.
     *
     * @param registrationNumber    Registration number of the vehicle.
     * @return                      Flow with a list of services.
     */
    fun getServices(registrationNumber: String): Flow<List<Service>> =
        serviceDao.getServices(registrationNumber)

    /**
     * Get a service by id.
     *
     * @param id    The id of the service to get.
     * @return      Flow with a service or null if not found.
     */
    fun getService(id: UUID): Flow<Service?> = serviceDao.getService(id)

    /**
     * Insert a service into the database.
     *
     * @param service   The service to insert.
     */
    suspend fun insert(service: Service) {
        serviceDao.insert(service)
    }

    /**
     * Update a service in the database.
     *
     * @param service   The updated service.
     */
    suspend fun update(service: Service) {
        serviceDao.update(service)
    }

    /**
     * Delete a service from the database.
     *
     * @param id    The id of the service to delete.
     */
    suspend fun deleteService(id: UUID) {
        serviceDao.delete(id)
    }

    // ========================================================================
    //                              Reminders
    // ========================================================================

    /**
     * Get all reminders for a vehicle.
     *
     * @param registrationNumber    The registration number of the vehicle.
     * @return                      A flow with a list of reminders.
     */
    fun getReminders(registrationNumber: String): Flow<List<Reminder>> =
        reminderDao.getReminders(registrationNumber)


    /**
     * Get a reminder by id.
     *
     * @param id    The id of the reminder to get.
     * @return      Flow with a reminder or null if not found.
     */
    fun getReminder(id: UUID): Flow<Reminder?> =
        reminderDao.getReminder(id)

    /**
     * Insert a reminder into the database.
     *
     * @param reminder   The reminder to insert.
     */
    suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    /**
     * Update a reminder in the database.
     *
     * @param reminder   The updated reminder.
     */
    suspend fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    /**
     * Delete a reminder from the database.
     *
     * @param id    The id of the reminder to delete.
     */
    suspend fun deleteReminder(id: UUID) {
        reminderDao.delete(id)
    }
}