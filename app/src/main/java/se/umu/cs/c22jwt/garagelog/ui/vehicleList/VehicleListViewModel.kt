package se.umu.cs.c22jwt.garagelog.ui.vehicleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import se.umu.cs.c22jwt.garagelog.data.Reminder
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.database.VehicleRepository
import javax.inject.Inject

/**
 * A view model representing a vehicle list.
 *
 * @param repository            Repository for accessing the database.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@HiltViewModel
class VehicleListViewModel @Inject constructor(private val repository: VehicleRepository) :
    ViewModel() {
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getVehicles().collect { items ->
                _vehicles.value = items
            }
        }
    }

    /**
     * Get all reminders for a vehicle.
     *
     * @param registrationNumber    Registration number of the vehicle.
     */
    fun getReminders(registrationNumber: String): StateFlow<List<Reminder>> {
        val reminders = MutableStateFlow<List<Reminder>>(emptyList())
        viewModelScope.launch {
            repository.getReminders(registrationNumber).collect { items ->
                reminders.value = items
            }
        }
        return reminders
    }
}
