package se.umu.cs.c22jwt.garagelog.ui.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import se.umu.cs.c22jwt.garagelog.Routes
import se.umu.cs.c22jwt.garagelog.data.Reminder
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.ui.components.DeleteDialog
import se.umu.cs.c22jwt.garagelog.ui.components.ExpandableListItem
import se.umu.cs.c22jwt.garagelog.ui.components.UntilServiceText
import se.umu.cs.c22jwt.garagelog.ui.vehicle.VehicleViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

/**
 * Compose screen for viewing all reminders for a vehicle.
 *
 * @param viewModel         ViewModel of the vehicle.
 * @param navController     The navigation controller.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: VehicleViewModel, navController: NavHostController
) {
    val vehicle: Vehicle by viewModel.vehicle.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var selectedReminder: UUID? by rememberSaveable { mutableStateOf(null) }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Reminders")
                    Text(
                        vehicle.registrationNumber, style = MaterialTheme.typography.labelMedium
                    )
                }
            }, navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }, scrollBehavior = scrollBehavior
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                navController.navigate(
                    "${Routes.EditReminderScreen.route}?regN=${vehicle.registrationNumber}&id=&mileage=${vehicle.mileage}"
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Add, "Add reminder", Modifier)
        }
    }) { innerPadding ->
        ReminderList(
            reminders = reminders, vehicle = vehicle, onEdit = { id ->
                navController.navigate(
                    "${Routes.EditReminderScreen.route}?regN=${vehicle.registrationNumber}&id=$id&mileage=${vehicle.mileage}"
                )
            }, onDelete = { reminder ->
                selectedReminder = reminder.id
                showDeleteDialog = true
            }, modifier = Modifier.padding(innerPadding)
        )
    }

    if (showDeleteDialog) {
        DeleteDialog(onDismiss = {
            showDeleteDialog = false
        }, onDelete = {
            if (selectedReminder != null) {
                viewModel.deleteReminder(selectedReminder!!)
                selectedReminder = null
            }
        })
    }
}

/**
 * A composable list of reminders.
 *
 * @param reminders     The reminders for a vehicle.
 * @param vehicle       The vehicle.
 * @param onEdit        Called when pressing the edit button for a reminder.
 * @param onDelete      Called when pressing the delete button for a reminder.
 * @param modifier      [Modifier] to be applied.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun ReminderList(
    reminders: List<Reminder>,
    vehicle: Vehicle,
    onEdit: (UUID) -> Unit,
    onDelete: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = reminders, key = { it.id }) { reminder ->
            ExpandableListItem(
                headlineContent = { Text(reminder.title) },
                overlineContent = {
                    if (reminder.repeat) {
                        Text("Repeating")
                    }
                },
                supportingContent = {
                    UntilServiceText(reminder, vehicle.mileage ?: 0)
                },
                expandedContent = {
                    Column {
                        if (reminder.repeat) {
                            Text(
                                text = "Repeats every " + if (reminder.mileageInterval != 0 && reminder.dateInterval != 0) {
                                    "${reminder.mileageInterval} km or ${reminder.dateInterval} months."
                                } else if (reminder.mileageInterval != 0) {
                                    "${reminder.mileageInterval} km."
                                } else {
                                    "${reminder.dateInterval} months."
                                }, style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (!reminder.notes.isEmpty()) {
                            Text(
                                text = "Notes: ${reminder.notes}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row {
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { onEdit(reminder.id) }) {
                                Text("Edit")
                            }
                            TextButton(onClick = { onDelete(reminder) }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
            )
            HorizontalDivider()
        }
    }
}

@Composable
@Preview
fun ReminderListPreview() {
    val reminders = listOf(
        Reminder(
            UUID.randomUUID(), "Oil change", "ABC123", 7300, Date.from(
                LocalDate.now().minusMonths(8).atStartOfDay(
                    ZoneId.systemDefault()
                ).toInstant()
            ), 1000, 12, "", true
        ),
        Reminder(UUID.randomUUID(), "Service", "ABC123", 8342, Date(), 2000, 14, "", false),
    )
    val vehicle = Vehicle("ABC123", "", "", 8574, null)
    ReminderList(reminders, vehicle, {}, {})
}