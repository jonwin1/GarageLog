package se.umu.cs.c22jwt.garagelog.ui.vehicle

import android.content.Context
import androidx.lifecycle.SavedStateHandle
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
import java.io.File
import java.util.UUID

/**
 * A view model representing a vehicle.
 *
 * @param registrationNumber    Registration number of the vehicle the service belongs to.
 * @param repository            Repository for accessing the database.
 * @param state                 Saved state.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@HiltViewModel(assistedFactory = VehicleViewModel.Factory::class)
class VehicleViewModel @AssistedInject constructor(
    @Assisted private val registrationNumber: String,
    private val repository: VehicleRepository,
    private val state: SavedStateHandle,
) : ViewModel() {
    private val _vehicle: MutableStateFlow<Vehicle> = MutableStateFlow(Vehicle("", "", "", null))
    val vehicle: StateFlow<Vehicle> = _vehicle.asStateFlow()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    var isNewVehicle = registrationNumber == ""

    private val _imageFileName = MutableStateFlow<String?>(state["imageFileName"])
    val imageFileName = _imageFileName.asStateFlow()

    init {
        if (!isNewVehicle) {
            viewModelScope.launch {
                repository.getVehicle(registrationNumber).collect { item ->
                    if (item != null) {
                        _vehicle.value = item
                        // Only set if the user hasn't set a different image
                        if (_imageFileName.value == null && item.photoFileName != null) {
                            _imageFileName.value = item.photoFileName
                            state["imageFileName"] = item.photoFileName
                        }
                    }
                }
            }
            viewModelScope.launch {
                repository.getServices(registrationNumber).collect { items ->
                    _services.value = items
                }
            }
            viewModelScope.launch {
                repository.getReminders(registrationNumber).collect { items ->
                    _reminders.value = items
                }
            }
        }
    }

    /**
     * Reset the image file to the one saved in the database.
     *
     * @param context   Context.
     */
    fun resetImageFile(context: Context) {
        // Delete the previous image
        if (_imageFileName.value != vehicle.value.photoFileName) {
            _imageFileName.value?.let { File(context.filesDir, it).delete() }
        }

        _imageFileName.value = vehicle.value.photoFileName
        state["imageFileName"] = vehicle.value.photoFileName
    }

    /**
     * Set a new image and delete the old.
     *
     * @param context   Context.
     * @param file      The new image file, or null to remove.
     */
    fun setImageFile(context: Context, file: File?) {
        // Delete the old image if it's not the name saved in the database
        if (_imageFileName.value != file?.name && _imageFileName.value != vehicle.value.photoFileName) {
            _imageFileName.value?.let { File(context.filesDir, it).delete() }
        }

        _imageFileName.value = file?.name
        state["imageFileName"] = file?.name
    }

    /**
     * Check if a registration number is already taken.
     *
     * @param registrationNumber    The registration number to check.
     * @param setExists             Called to return the result when the coroutine finishes.
     */
    fun exists(
        registrationNumber: String, setExists: (Boolean) -> Unit
    ) = viewModelScope.launch {
        setExists(repository.exists(registrationNumber))
    }

    /**
     * Check if a vehicle is saved.
     *
     * @param vehicle   The potentially updated vehicle.
     * @return          True if saved, false otherwise.
     */
    fun isSaved(vehicle: Vehicle): Boolean {
        return vehicle == _vehicle.value
    }

    /**
     * Save a vehicle.
     *
     * @param context   Context.
     * @param vehicle   The updated vehicle.
     */
    fun save(context: Context, vehicle: Vehicle) {
        // Delete the previous image
        if (vehicle.photoFileName != this.vehicle.value.photoFileName) {
            this.vehicle.value.photoFileName?.let { File(context.filesDir, it).delete() }
        }

        if (!isSaved(vehicle)) {
            if (isNewVehicle) {
                viewModelScope.launch {
                    repository.insert(vehicle)
                }
            } else {
                viewModelScope.launch {
                    repository.update(vehicle)
                }
            }
        }
    }

    /**
     * Delete a vehicle.
     *
     * @param registrationNumber    Registration number of the vehicle to delete.
     */
    fun deleteVehicle(context: Context, registrationNumber: String) {
        _imageFileName.value?.let { File(context.filesDir, it).delete() }
        viewModelScope.launch {
            repository.deleteVehicle(registrationNumber)
        }
    }

    /**
     * Delete a service.
     *
     * @param id    Id of the service to delete.
     */
    fun deleteService(id: UUID) = viewModelScope.launch {
        repository.deleteService(id)
    }

    /**
     * Delete a reminder.
     *
     * @param id    Id of the reminder to delete.
     */
    fun deleteReminder(id: UUID) = viewModelScope.launch {
        repository.deleteReminder(id)
    }

    /**
     * Assisted factory for injecting the parameters when creating the VehicleViewModel.
     */
    @AssistedFactory
    interface Factory {
        fun create(registrationNumber: String): VehicleViewModel
    }
}