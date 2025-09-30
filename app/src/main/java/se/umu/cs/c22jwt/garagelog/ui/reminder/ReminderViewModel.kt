package se.umu.cs.c22jwt.garagelog.ui.reminder

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
import se.umu.cs.c22jwt.garagelog.database.VehicleRepository
import java.util.Date
import java.util.UUID

/**
 * A view model representing a reminder.
 *
 * @param registrationNumber    Registration number of the vehicle the reminder belongs to.
 * @param reminderId            The id of the reminder.
 * @param vehicleMileage        The mileage of the vehicle linked to the reminder.
 * @param repository            Repository for accessing application data.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@HiltViewModel(assistedFactory = ReminderViewModel.Factory::class)
class ReminderViewModel @AssistedInject constructor(
    @Assisted private val registrationNumber: String,
    @Assisted private val reminderId: UUID,
    @Assisted private val vehicleMileage: Int,
    private val repository: VehicleRepository,
) : ViewModel() {
    private val _reminder: MutableStateFlow<Reminder?> = MutableStateFlow(null)
    val reminder: StateFlow<Reminder?> = _reminder.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getReminder(reminderId).collect { item ->
                if (item != null) {
                    _reminder.value = item
                }
            }
        }
    }

    /**
     * Whether the reminder with the provided values is already saved.
     *
     * @returns True if it is already saved and otherwise false.
     *
     * @see Reminder
     */
    fun isSaved(
        title: String?, mileageInterval: Int?, dateInterval: Int?, notes: String?, repeat: Boolean?
    ): Boolean {
        if (reminder.value == null) {
            return title == null && mileageInterval == null && dateInterval == null && notes == null && repeat == null
        }

        return reminder.value!!.copy(
            title = title ?: "Reminder",
            mileageInterval = mileageInterval ?: 0,
            dateInterval = dateInterval ?: 0,
            notes = notes ?: "",
            repeat = repeat ?: false
        ) == reminder.value
    }

    /**
     * Update or create a reminder with the given values and save it to the database.
     *
     * @see Reminder
     */
    fun save(
        title: String?, mileageInterval: Int?, dateInterval: Int?, notes: String?, repeat: Boolean?
    ) {
        val updatedReminder: Reminder

        if (reminder.value == null) {
            updatedReminder = Reminder(
                reminderId,
                title ?: "Reminder",
                registrationNumber,
                vehicleMileage,
                Date(),
                mileageInterval ?: 0,
                dateInterval ?: 0,
                notes ?: "",
                repeat ?: false
            )
            viewModelScope.launch {
                repository.insert(updatedReminder)
            }
        } else {
            updatedReminder = reminder.value!!.copy(
                title = title ?: "Reminder",
                mileageInterval = mileageInterval ?: 0,
                dateInterval = dateInterval ?: 0,
                notes = notes ?: "",
                repeat = repeat ?: false
            )
            viewModelScope.launch {
                repository.update(updatedReminder)
            }
        }
    }

    /**
     * Assisted factory for inserting the parameters when creating the ReminderViewModel.
     */
    @AssistedFactory
    interface Factory {
        fun create(
            registrationNumber: String, reminderId: UUID, vehicleMileage: Int
        ): ReminderViewModel
    }
}
