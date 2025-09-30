package se.umu.cs.c22jwt.garagelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dagger.hilt.android.AndroidEntryPoint
import se.umu.cs.c22jwt.garagelog.ui.components.StatusBarProtection
import se.umu.cs.c22jwt.garagelog.ui.reminder.EditReminderScreen
import se.umu.cs.c22jwt.garagelog.ui.reminder.ReminderViewModel
import se.umu.cs.c22jwt.garagelog.ui.reminder.RemindersScreen
import se.umu.cs.c22jwt.garagelog.ui.service.EditServiceScreen
import se.umu.cs.c22jwt.garagelog.ui.service.ServiceHistoryScreen
import se.umu.cs.c22jwt.garagelog.ui.service.ServiceViewModel
import se.umu.cs.c22jwt.garagelog.ui.theme.GarageLogTheme
import se.umu.cs.c22jwt.garagelog.ui.vehicle.EditVehicleScreen
import se.umu.cs.c22jwt.garagelog.ui.vehicle.VehicleScreen
import se.umu.cs.c22jwt.garagelog.ui.vehicle.VehicleViewModel
import se.umu.cs.c22jwt.garagelog.ui.vehicleList.VehicleListScreen
import se.umu.cs.c22jwt.garagelog.ui.vehicleList.VehicleListViewModel
import java.util.UUID

/**
 * The main activity and entry point of the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            GarageLogTheme {
                NavigationComponent(navController)
            }
        }
    }
}

/**
 * The navigation component for the application, handles all destinations that can be navigated to.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
private fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController, startDestination = Routes.VehicleListScreen.route
    ) {
        composable(Routes.VehicleListScreen.route) {
            val viewModel = hiltViewModel<VehicleListViewModel>()
            VehicleListScreen(viewModel, navController)
        }

        navigation(
            route = Routes.VehicleParent.route,
            startDestination = "${Routes.VehicleScreen.route}?id={id}"
        ) {
            composable(
                route = "${Routes.VehicleScreen.route}?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Routes.VehicleParent.route)
                }
                val registrationNumber = entry.arguments?.getString("id") ?: ""
                // Create a new ViewModel if none exists, otherwise get it from parent
                val viewModel = hiltViewModel<VehicleViewModel, VehicleViewModel.Factory>(
                    viewModelStoreOwner = parentEntry, creationCallback = { factory ->
                        factory.create(registrationNumber)
                    })
                VehicleScreen(viewModel, navController)
                StatusBarProtection()
            }

            composable(
                route = "${Routes.EditVehicleScreen.route}?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Routes.VehicleParent.route)
                }
                val registrationNumber = entry.arguments?.getString("id") ?: ""
                // Create a new ViewModel if none exists, otherwise get it from parent
                val viewModel = hiltViewModel<VehicleViewModel, VehicleViewModel.Factory>(
                    viewModelStoreOwner = parentEntry, creationCallback = { factory ->
                        factory.create(registrationNumber)
                    })
                EditVehicleScreen(viewModel) { navController.navigateUp() }
            }
        }

        composable(
            route = "${Routes.EditServiceScreen.route}?regN={regN}&id={id}", arguments = listOf(
                navArgument("regN") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val registrationNumber = entry.arguments?.getString("regN") ?: ""
            val serviceIdString = entry.arguments?.getString("id") ?: ""
            val serviceId = if (!serviceIdString.isEmpty()) {
                UUID.fromString(serviceIdString)
            } else {
                UUID.randomUUID()
            }
            val viewModel = hiltViewModel<ServiceViewModel, ServiceViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(registrationNumber, serviceId)
                })
            EditServiceScreen(viewModel) { navController.navigateUp() }
        }

        composable(
            route = "${Routes.ServiceHistoryScreen.route}?regN={regN}", arguments = listOf(
                navArgument("regN") { type = NavType.StringType })
        ) { entry ->
            val registrationNumber = entry.arguments?.getString("regN") ?: ""
            val viewModel = hiltViewModel<VehicleViewModel, VehicleViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(registrationNumber)
                })
            ServiceHistoryScreen(viewModel, navController)
        }

        composable(
            route = "${Routes.RemindersScreen.route}?regN={regN}", arguments = listOf(
                navArgument("regN") { type = NavType.StringType })
        ) { entry ->
            val registrationNumber = entry.arguments?.getString("regN") ?: ""
            val viewModel = hiltViewModel<VehicleViewModel, VehicleViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(registrationNumber)
                })
            RemindersScreen(viewModel, navController)
        }

        composable(
            route = "${Routes.EditReminderScreen.route}?regN={regN}&id={id}&mileage={mileage}",
            arguments = listOf(
                navArgument("regN") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("mileage") { type = NavType.IntType })
        ) { entry ->
            val registrationNumber = entry.arguments?.getString("regN") ?: ""
            val reminderIdString = entry.arguments?.getString("id") ?: ""
            val mileage = entry.arguments?.getInt("mileage") ?: 0

            val reminderId = if (!reminderIdString.isEmpty()) {
                UUID.fromString(reminderIdString)
            } else {
                UUID.randomUUID()
            }

            val viewModel = hiltViewModel<ReminderViewModel, ReminderViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(registrationNumber, reminderId, mileage)
                })
            EditReminderScreen(viewModel) { navController.navigateUp() }
        }
    }
}