package se.umu.cs.c22jwt.garagelog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import se.umu.cs.c22jwt.garagelog.R

/**
 * A list item button that launches the camera app to take a photo.
 *
 * @param requestTakePhoto  Called to request a photo to be taken.
 * @param modifier          [Modifier] to be applied to the list item.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun TakePhotoButton(
    requestTakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable {
            requestTakePhoto()
        }, headlineContent = {
            Text("Take a picture")
        }, leadingContent = {
            Icon(
                painter = painterResource(R.drawable.outline_add_a_photo_24),
                contentDescription = "Take a picture"
            )
        }, colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}