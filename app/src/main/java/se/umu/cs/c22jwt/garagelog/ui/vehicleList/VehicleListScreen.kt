package se.umu.cs.c22jwt.garagelog.ui.vehicleList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import se.umu.cs.c22jwt.garagelog.Routes
import se.umu.cs.c22jwt.garagelog.data.Vehicle

/**
 * The main screen of the application with a list of all vehicles.
 *
 * @param viewModel     View model for the vehicle list.
 * @param navController The navigation controller.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    viewModel: VehicleListViewModel, navController: NavHostController
) {
    val vehicles by viewModel.vehicles.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My garage")
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.EditVehicleScreen.route + "?id=" + "")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Add vehicle")
            }
        }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        VehicleList(vehicles, viewModel, innerPadding, navController)
    }
}

/**
 * Composable list of vehicles.
 *
 * @param vehicles      The vehicle to show in the list.
 * @param viewModel     View model of the vehicle list.
 * @param innerPadding  Inner padding to apply.
 * @param navController The navigation controller.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun VehicleList(
    vehicles: List<Vehicle>,
    viewModel: VehicleListViewModel,
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .padding(horizontal = 12.dp)
    ) {
        items(vehicles) { vehicle ->
            val reminders by viewModel.getReminders(vehicle.registrationNumber).collectAsState()
            VehicleCard(
                vehicle, reminders, navController, Modifier.padding(4.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.padding(innerPadding.calculateBottomPadding()))
        }
    }
}