package se.umu.cs.c22jwt.garagelog

/**
 * Defines all routes that can be navigated to.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
sealed class Routes(val route: String) {
    data object VehicleListScreen : Routes("vehicleList")

    data object VehicleParent : Routes("vehicleParent")
    data object VehicleScreen : Routes("vehicle")
    data object EditVehicleScreen : Routes("editVehicle")

    data object ServiceHistoryScreen : Routes("serviceHistory")
    data object EditServiceScreen : Routes("editService")

    data object RemindersScreen : Routes("reminders")
    data object EditReminderScreen : Routes("editReminder")
}