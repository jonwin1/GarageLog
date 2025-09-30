package se.umu.cs.c22jwt.garagelog.ui.service

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.launch
import se.umu.cs.c22jwt.garagelog.data.Service
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Compose screen for editing a service.
 *
 * @param viewModel         ViewModel of the service.
 * @param navigateBack      Called to navigate back to the previous page.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditServiceScreen(
    viewModel: ServiceViewModel,
    navigateBack: () -> Unit,
) {
    val serviceState: Service by viewModel.service.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }

    var showDateDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var title by rememberSaveable { mutableStateOf("") }
    var titleIsValid by rememberSaveable { mutableStateOf(true) }
    var date by rememberSaveable { mutableStateOf(Date()) }
    var mileage by rememberSaveable { mutableIntStateOf(0) }
    var cost by rememberSaveable { mutableIntStateOf(0) }
    var notes by rememberSaveable { mutableStateOf("") }

    var completes by rememberSaveable { mutableStateOf(mapOf<UUID, Boolean>()) }

    // Warn the user if trying to navigate back without saving.
    BackHandler {
        val service = Service(
            serviceState.id, title, serviceState.registrationNumber, date, mileage, cost, notes
        )
        if (viewModel.isSaved(service)) {
            navigateBack()
        } else {
            showExitDialog = true
        }
    }

    // Set values when the service has been received from the database without
    // overwriting user entered values.
    LaunchedEffect(serviceState) {
        if (title.isEmpty()) {
            title = serviceState.title
        }
        if (dateFormatter.format(date) == dateFormatter.format(Date())) {
            date = serviceState.date
        }
        if (mileage == 0) {
            mileage = serviceState.mileage
        }
        if (cost == 0) {
            cost = serviceState.cost
        }
        if (notes.isEmpty()) {
            notes = serviceState.notes
        }
        if (completes.isEmpty()) {
            completes = reminders.associate { it.id to false }.toMutableMap()
        }
    }

    Scaffold(
        topBar = {
            val service = Service(
                serviceState.id, title, serviceState.registrationNumber, date, mileage, cost, notes
            )
            val isSaved = viewModel.isSaved(service)

            EditServiceTopBar(
                scrollBehavior = scrollBehavior,
                isNewService = viewModel.isNewService,
                enableSave = !isSaved && !title.isEmpty(),
                onClose = {
                    if (isSaved) {
                        navigateBack()
                    } else {
                        showExitDialog = true
                    }
                },
                onSave = {
                    viewModel.save(service)
                    reminders.forEach { reminder ->
                        if (completes[reminder.id] == true) {
                            if (reminder.repeat) {
                                val updatedReminder =
                                    reminder.copy(mileage = service.mileage, date = service.date)
                                viewModel.update(updatedReminder)
                            } else {
                                viewModel.deleteReminder(reminder.id)
                            }
                        }
                    }
                    navigateBack()
                })
        }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleIsValid = !title.isEmpty() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    label = { Text("Title*") },
                    supportingText = { Text("*required") },
                    singleLine = true,
                    isError = !titleIsValid,
                    trailingIcon = {
                        if (!titleIsValid) {
                            Icon(Icons.Default.Warning, "Warning icon")
                        } else null
                    })
            }

            item {
                OutlinedTextField(
                    value = dateFormatter.format(date),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    trailingIcon = {
                        IconButton(onClick = {
                            showDateDialog = true
                        }) { Icon(Icons.Default.DateRange, "Pick date") }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = if (mileage > 0) mileage.toString() else "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            mileage = 0
                        } else if (it.isDigitsOnly() && it.length < Int.MAX_VALUE.toString().length) {
                            mileage = it.toInt()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    label = { Text("Mileage") },
                    suffix = { Text("km") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                OutlinedTextField(
                    value = if (cost > 0) cost.toString() else "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            cost = 0
                        } else if (it.isDigitsOnly() && it.length < Int.MAX_VALUE.toString().length) {
                            cost = it.toInt()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    label = { Text("Cost") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    },
                    label = { Text("Notes") },
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .bringIntoViewRequester(bringIntoViewRequester)
                )
            }

            if (viewModel.isNewService && !reminders.isEmpty()) {
                item {
                    Text(
                        text = "Completes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(items = reminders, key = { it.id }) { reminder ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = completes[reminder.id] ?: false, onCheckedChange = {
                                completes =
                                    completes.toMutableMap().apply { this[reminder.id] = it }
                            }, modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text = reminder.title,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Discard changes?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                        navigateBack()
                    }) { Text("Discard") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                    }) { Text("Cancel") }
                },
            )
        }

        if (showDateDialog) {
            DatePickerDialog(
                onDismissRequest = { showDateDialog = false }, confirmButton = {
                    TextButton(
                        onClick = {
                            showDateDialog = false
                            if (datePickerState.selectedDateMillis != null) {
                                date = Date(datePickerState.selectedDateMillis!!)
                            }
                        },
                        enabled = datePickerState.selectedDateMillis != null,
                    ) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = { showDateDialog = false }) { Text("Cancel") }
                }, modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                DatePicker(
                    state = datePickerState, modifier = Modifier
                )
            }
        }
    }
}

/**
 * Top bar for the edit service screen.
 *
 * @param scrollBehavior    Scroll behaviour of the top bar.
 * @param isNewService      Whether it is a new service or not.
 * @param enableSave        Whether to enable the save button
 * @param onClose           Called when the close button is pressed.
 * @param onSave            Called when the save button is pressed.
 * @param modifier          [Modifier] to be applied.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditServiceTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    isNewService: Boolean,
    enableSave: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier, title = {
            if (isNewService) {
                Text("New service")
            } else {
                Text("Edit service")
            }
        }, navigationIcon = {
            IconButton(onClick = { onClose() }) {
                Icon(Icons.Default.Close, "Close")
            }
        }, actions = {
            TextButton(onClick = { onSave() }, enabled = enableSave) {
                Text("Save")
            }
        }, scrollBehavior = scrollBehavior
    )
}
