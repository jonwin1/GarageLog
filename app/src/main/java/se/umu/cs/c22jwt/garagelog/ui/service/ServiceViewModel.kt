package se.umu.cs.c22jwt.garagelog.ui.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import se.umu.cs.c22jwt.garagelog.data.Reminder
import se.umu.cs.c22jwt.garagelog.data.Service
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.database.VehicleRepository
import java.util.Date
import java.util.UUID

/**
 * A view model representing a service entry.
 *
 * @param registrationNumber    Registration number of the vehicle the service belongs to.
 * @param serviceId             The id of the service.
 * @param repository            Repository for accessing the database.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@HiltViewModel(assistedFactory = ServiceViewModel.Factory::class)
class ServiceViewModel @AssistedInject constructor(
    @Assisted private val registrationNumber: String,
    @Assisted private val serviceId: UUID,
    private val repository: VehicleRepository,
) : ViewModel() {
    private val _service: MutableStateFlow<Service> = MutableStateFlow(
        Service(serviceId, "", registrationNumber, Date(), 0, 0, "")
    )
    val service: StateFlow<Service> = _service.asStateFlow()

    private val _vehicle: MutableStateFlow<Vehicle> = MutableStateFlow(Vehicle("", "", "", null))
    val vehicle: StateFlow<Vehicle> = _vehicle.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    var isNewService = true

    init {
        viewModelScope.launch {
            repository.getService(serviceId).collect { item ->
                if (item != null) {
                    _service.value = item
                    isNewService = false
                }
            }
        }
        viewModelScope.launch {
            repository.getVehicle(registrationNumber).collect { item ->
                if (item != null) {
                    _vehicle.value = item
                }
            }
        }
        viewModelScope.launch {
            repository.getReminders(registrationNumber).collect { items ->
                _reminders.value = items
            }
        }
    }

    /**
     * Whether the service has been saved.
     *
     * @param service   The potentially updated service.
     * @returns         True if it is already saved, otherwise false.
     */
    fun isSaved(service: Service): Boolean {
        return service == _service.value
    }

    /**
     * Save a reminder to the database.
     *
     * @param service   The service to save.
     */
    fun save(service: Service) {
        if (isNewService) {
            viewModelScope.launch {
                repository.insert(service)
            }
        } else {
            viewModelScope.launch {
                repository.update(service)
            }
        }

        if (vehicle.value.mileage != null && service.mileage > vehicle.value.mileage!!) {
            val updatedVehicle = vehicle.value.copy(mileage = service.mileage)
            viewModelScope.launch {
                repository.update(updatedVehicle)
            }
        }
    }

    /**
     * Update a reminder in the database.
     *
     * @param reminder  The updated reminder.
     */
    fun update(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    /**
     * Delete a reminder from the database.
     *
     * @param id    Id of the reminder to delete.
     */
    fun deleteReminder(id: UUID) = viewModelScope.launch {
        repository.deleteReminder(id)
    }

    /**
     * Assisted factory for injecting the parameters when creating the ServiceViewModel.
     */
    @AssistedFactory
    interface Factory {
        fun create(registrationNumber: String, serviceId: UUID): ServiceViewModel
    }
}
