package se.umu.cs.c22jwt.garagelog.ui.vehicleList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import se.umu.cs.c22jwt.garagelog.Routes
import se.umu.cs.c22jwt.garagelog.data.Reminder
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.ui.components.UntilServiceText
import java.io.File

/**
 * A composable card representing a vehicle.
 *
 * @param vehicle       The vehicle to show on the card.
 * @param reminders     A list of all reminders for the vehicle.
 * @param navController The navigation controller.
 * @param modifier      [Modifier] to apply.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun VehicleCard(
    vehicle: Vehicle,
    reminders: List<Reminder>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            navController.navigate(
                Routes.VehicleScreen.route + "?id=" + vehicle.registrationNumber
            )
        }, modifier = modifier.aspectRatio(1f)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(
                if (vehicle.photoFileName != null) {
                    File(
                        LocalContext.current.filesDir, vehicle.photoFileName
                    )
                } else null
            ).crossfade(true).build(),
            contentDescription = "Image of vehicle",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
            error = painterResource(android.R.drawable.ic_menu_gallery),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = vehicle.name, style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = vehicle.registrationNumber, style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "Mileage: ${vehicle.mileage.toString()} km",
                style = MaterialTheme.typography.labelMedium
            )
            UntilServiceText(
                reminders = reminders,
                currentMileage = vehicle.mileage ?: 0,
                leadingText = "Service in",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}