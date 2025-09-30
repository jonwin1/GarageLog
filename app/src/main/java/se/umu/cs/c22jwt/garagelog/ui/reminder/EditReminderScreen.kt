package se.umu.cs.c22jwt.garagelog.ui.reminder

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import se.umu.cs.c22jwt.garagelog.data.Reminder

/**
 * Compose screen for editing a reminder.
 *
 * @param viewModel         ViewModel of the reminder.
 * @param navigateBack      Called to navigate back to the previous page.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderScreen(
    viewModel: ReminderViewModel,
    navigateBack: () -> Unit,
) {
    val reminderState: Reminder? by viewModel.reminder.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }

    var title: String? by rememberSaveable { mutableStateOf(null) }
    var mileageInterval: Int? by rememberSaveable { mutableStateOf(null) }
    var dateInterval: Int? by rememberSaveable { mutableStateOf(null) }
    var notes: String? by rememberSaveable { mutableStateOf(null) }
    var repeat: Boolean? by rememberSaveable { mutableStateOf(null) }

    var titleIsValid by rememberSaveable { mutableStateOf(true) }
    var intervalIsValid by rememberSaveable { mutableStateOf(true) }
    fun checkIntervalValidity(): Boolean {
        val mileageIsValid = (mileageInterval != null && mileageInterval!! > 0)
        val dateIsValid = (dateInterval != null && dateInterval!! > 0)
        return mileageIsValid || dateIsValid
    }

    // Warn the user if trying to navigate back without saving.
    BackHandler {
        if (viewModel.isSaved(title, mileageInterval, dateInterval, notes, repeat)) {
            navigateBack()
        } else {
            showExitDialog = true
        }
    }

    // Set values when the reminder has been received from the database without
    // overwriting user entered values.
    LaunchedEffect(reminderState) {
        if (reminderState != null) {
            if (title == null) {
                title = reminderState!!.title
            }
            if (mileageInterval == null) {
                mileageInterval = reminderState!!.mileageInterval
            }
            if (dateInterval == null) {
                dateInterval = reminderState!!.dateInterval
            }
            if (notes == null) {
                notes = reminderState!!.notes
            }
            if (repeat == null) {
                repeat = reminderState!!.repeat
            }
        }
    }

    Scaffold(
        topBar = {
            val isSaved = viewModel.isSaved(title, mileageInterval, dateInterval, notes, repeat)

            EditReminderTopBar(
                scrollBehavior,
                reminderState == null,
                !isSaved && title != null && !title!!.isEmpty() && checkIntervalValidity(),
                {
                    if (isSaved) {
                        navigateBack()
                    } else {
                        showExitDialog = true
                    }
                },
                {
                    viewModel.save(title, mileageInterval, dateInterval, notes, repeat)
                    navigateBack()
                })
        }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title ?: "",
                    onValueChange = {
                        title = it
                        titleIsValid = title != null && !title!!.isEmpty()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                Text(
                    text = "Remind in",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row {
                    OutlinedTextField(
                        value = if (mileageInterval != null && mileageInterval!! > 0) {
                            mileageInterval.toString()
                        } else {
                            ""
                        },
                        onValueChange = {
                            if (it.isEmpty()) {
                                mileageInterval = 0
                            } else if (it.isDigitsOnly() && it.length < Int.MAX_VALUE.toString().length) {
                                mileageInterval = it.toInt()
                            }
                            intervalIsValid = checkIntervalValidity()
                        },
                        label = { Text("Kilometers*") },
                        singleLine = true,
                        isError = !intervalIsValid,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .weight(1f)
                    )

                    OutlinedTextField(
                        value = if (dateInterval != null && dateInterval!! > 0) {
                            dateInterval.toString()
                        } else {
                            ""
                        },
                        onValueChange = {
                            if (it.isEmpty()) {
                                dateInterval = 0
                            } else if (it.isDigitsOnly() && it.length < Int.MAX_VALUE.toString().length) {
                                dateInterval = it.toInt()
                            }
                            intervalIsValid = checkIntervalValidity()
                        },
                        label = { Text("Months*") },
                        singleLine = true,
                        isError = !intervalIsValid,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                    )
                }

                Text(
                    text = "*at least one required",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (intervalIsValid) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 4.dp,
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = repeat ?: false,
                        onCheckedChange = { repeat = it },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "Auto repeat", modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = notes ?: "",
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
                        .bringIntoViewRequester(bringIntoViewRequester)
                )
            }

            item {
                Spacer(Modifier.height(innerPadding.calculateBottomPadding()))
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
    }
}

/**
 * Top bar for the edit reminder screen.
 *
 * @param scrollBehavior    Scroll behaviour of the top bar.
 * @param isNewReminder     Whether it is a new reminder or not.
 * @param enableSave        Whether to enable the save button
 * @param onClose           Called when the close button is pressed.
 * @param onSave            Called when the save button is pressed.
 * @param modifier          [Modifier] to be applied.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    isNewReminder: Boolean,
    enableSave: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier, title = {
            if (isNewReminder) {
                Text("New reminder")
            } else {
                Text("Edit reminder")
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
