package se.umu.cs.c22jwt.garagelog.ui.service

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
import androidx.navigation.NavHostController
import se.umu.cs.c22jwt.garagelog.Routes
import se.umu.cs.c22jwt.garagelog.data.Service
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.ui.components.DeleteDialog
import se.umu.cs.c22jwt.garagelog.ui.components.ExpandableListItem
import se.umu.cs.c22jwt.garagelog.ui.vehicle.VehicleViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

/**
 * Compose screen for viewing all service entries for a vehicle.
 *
 * @param viewModel         ViewModel of the vehicle.
 * @param navController     The navigation controller.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistoryScreen(
    viewModel: VehicleViewModel, navController: NavHostController
) {
    val vehicle: Vehicle by viewModel.vehicle.collectAsState()
    val services by viewModel.services.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var selectedService: UUID? by rememberSaveable { mutableStateOf(null) }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Service history")
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
                    "${Routes.EditServiceScreen.route}?regN=${vehicle.registrationNumber}&id="
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Add, "Add service", Modifier)
        }
    }) { innerPadding ->
        ServiceList(
            services = services, onEdit = { id ->
                navController.navigate(
                    "${Routes.EditServiceScreen.route}?regN=${vehicle.registrationNumber}&id=$id"
                )
            }, onDelete = { service ->
                selectedService = service.id
                showDeleteDialog = true
            }, modifier = Modifier.padding(innerPadding)
        )
    }

    if (showDeleteDialog) {
        DeleteDialog(onDismiss = {
            showDeleteDialog = false
        }, onDelete = {
            if (selectedService != null) {
                viewModel.deleteService(selectedService!!)
                selectedService = null
            }
        })
    }
}

/**
 * A composable list of service items.
 *
 * @param services      The services for a vehicle.
 * @param onEdit        Called when pressing the edit button for a service item.
 * @param onDelete      Called when pressing the delete button for a service item.
 * @param modifier      [Modifier] to be applied.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun ServiceList(
    services: List<Service>,
    onEdit: (UUID) -> Unit,
    onDelete: (Service) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LazyColumn(
        modifier = modifier
    ) {
        items(items = services, key = { it.id }) { service ->
            ExpandableListItem(
                headlineContent = { Text(service.title) },
                expandedContent = {
                    Column {
                        Text(
                            text = "Cost: ${service.cost}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (!service.notes.isEmpty()) {
                            Text(
                                text = service.notes, style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row {
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { onEdit(service.id) }) {
                                Text("Edit")
                            }
                            TextButton(onClick = { onDelete(service) }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
                overlineContent = { Text(dateFormatter.format(service.date)) },
                supportingContent = { Text("${service.mileage} km") })
            HorizontalDivider()
        }
    }
}