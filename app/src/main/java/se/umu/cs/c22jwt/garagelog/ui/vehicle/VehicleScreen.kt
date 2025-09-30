package se.umu.cs.c22jwt.garagelog.ui.vehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import se.umu.cs.c22jwt.garagelog.Routes
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.ui.components.DeleteDialog
import se.umu.cs.c22jwt.garagelog.ui.components.ExpandableListItem
import se.umu.cs.c22jwt.garagelog.ui.components.UntilServiceText
import java.io.File

/**
 * Compose screen for showing a vehicle and information about it.
 *
 * @param viewModel         ViewModel of the vehicle.
 * @param navController     The navigation controller.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(
    viewModel: VehicleViewModel, navController: NavHostController
) {
    val context = LocalContext.current
    val vehicle: Vehicle by viewModel.vehicle.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showMileageDialog by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val imageFileName by viewModel.imageFileName.collectAsState()
    val imageFile = imageFileName?.let { File(context.filesDir, it) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            VehicleScreenTopBar(
                { navController.navigateUp() },
                { showBottomSheet = true },
                scrollBehavior
            )
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageFile)
                        .crossfade(true).build(),
                    contentDescription = "Image of vehicle",
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredSizeIn(minHeight = 128.dp, maxHeight = 256.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                    error = painterResource(android.R.drawable.ic_menu_gallery)
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            val sidePaddedModifier = Modifier.padding(horizontal = 16.dp)

            item {
                Text(
                    text = vehicle.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = sidePaddedModifier
                )
            }

            item {
                Text(
                    text = vehicle.registrationNumber,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = sidePaddedModifier.padding(bottom = 8.dp)
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = { Text("Mileage") },
                    supportingContent = {
                        Column {
                            Text(vehicle.mileage.toString() + " km")
                            UntilServiceText(reminders, vehicle.mileage ?: 0, "Service in")
                        }
                    },
                    trailingContent = { Icon(Icons.Default.Edit, "Update mileage") },
                    modifier = Modifier.clickable { showMileageDialog = true })
                HorizontalDivider()
            }

            if (!vehicle.notes.isEmpty()) {
                item {
                    ExpandableListItem(headlineContent = { Text("Notes") }, expandedContent = {
                        Text(
                            text = vehicle.notes, style = MaterialTheme.typography.bodyMedium
                        )
                    })
                    HorizontalDivider()
                }
            }

            item {
                ListItem(headlineContent = { Text("Service history") }, trailingContent = {
                    Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "Go to services")
                }, modifier = Modifier.clickable {
                    navController.navigate(
                        Routes.ServiceHistoryScreen.route + "?regN=${vehicle.registrationNumber}"
                    )
                })

                HorizontalDivider()
            }

            item {
                ListItem(headlineContent = { Text("Reminders") }, trailingContent = {
                    Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "Go to reminders")
                }, modifier = Modifier.clickable {
                    navController.navigate(
                        Routes.RemindersScreen.route + "?regN=${vehicle.registrationNumber}"
                    )
                })

                HorizontalDivider()
            }

            item { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
        }
    }

    if (showMileageDialog) {
        UpdateMileageDialog(vehicle.mileage, { showMileageDialog = false }, { newMileage ->
            val updatedVehicle = vehicle.copy(mileage = newMileage)
            val isSaved = viewModel.isSaved(updatedVehicle)
            if (!isSaved) {
                viewModel.save(context, updatedVehicle)
            }
        })
    }

    if (showBottomSheet) {
        VehicleBottomSheet(
            { showBottomSheet = false },
            { navController.navigate(Routes.EditVehicleScreen.route + "?id=" + vehicle.registrationNumber) },
            { showDeleteDialog = true },
            bottomSheetState,
        )
    }

    if (showDeleteDialog) {
        DeleteDialog(onDismiss = {
            showDeleteDialog = false
        }, onDelete = {
            viewModel.deleteVehicle(context, vehicle.registrationNumber)
            navController.navigateUp()
        })
    }
}

/**
 * Dialog for updating the mileage of a vehicle, warns if new mileage is less than previous.
 *
 * @param mileage   Current mileage of the vehicle.
 * @param onDismiss Called when closing the dialog.
 * @param onSave    Called to update the mileage with a new value.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun UpdateMileageDialog(mileage: Int?, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var value: Int? by rememberSaveable { mutableStateOf(null) }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New mileage") },
        text = {
            OutlinedTextField(
                value = (value ?: "").toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        value = null
                    } else if (it.isDigitsOnly() && it.length < Int.MAX_VALUE.toString().length) {
                        value = it.toInt()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("km") },
                placeholder = { Text((mileage ?: 0).toString()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val newMileage = value ?: mileage ?: 0
                if (newMileage < (mileage ?: 0)) {
                    showConfirmDialog = true
                } else {
                    onSave(newMileage)
                    onDismiss()
                }
            }) { Text("Ok") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("New mileage is less") },
            text = { Text("The entered value is less than the previous mileage, are you sure you want to continue?") },
            confirmButton = {
                TextButton(onClick = {
                    onSave(value ?: mileage ?: 0)
                    showConfirmDialog = false
                    onDismiss()
                }) { Text("Continue") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                }) { Text("Cancel") }
            },
        )
    }
}

/**
 * Bottom sheet with more controls for the vehicle.
 *
 * @param onDismiss         Called when closing the sheet.
 * @param onEdit            Called to edit a vehicle.
 * @param onDelete          Called to delete a vehicle.
 * @param bottomSheetState  The state of the bottom sheet.
 * @param modifier          [Modifier] to apply.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleBottomSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    bottomSheetState: SheetState,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = bottomSheetState, modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            ListItem(
                modifier = Modifier.clickable { onEdit(); onDismiss() },
                headlineContent = { Text("Edit vehicle") },
                leadingContent = { Icon(Icons.Default.Edit, "Edit vehicle icon") },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
            ListItem(
                modifier = Modifier.clickable { onDelete(); onDismiss() },
                headlineContent = { Text("Delete vehicle") },
                leadingContent = { Icon(Icons.Default.Delete, "Delete vehicle icon") },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    headlineColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error,
                )
            )
        }
    }
}

/**
 * Top bar for the vehicle screen.
 *
 * @param onNavigateBack    Called when pressing the back button.
 * @param onMenu            Called to open a menu with more controls for the vehicle.
 * @param scrollBehavior    Scroll behaviour of the top bar.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreenTopBar(
    onNavigateBack: () -> Unit,
    onMenu: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {}, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent
        ), navigationIcon = {
            FilledIconButton(
                onClick = onNavigateBack, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(.6f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }, actions = {
            FilledIconButton(
                onClick = onMenu, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(.6f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }, scrollBehavior = scrollBehavior
    )
}