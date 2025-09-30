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
 * A list item button that opens a photo picker.
 *
 * @param requestPickPhoto  Called to request a photo to be picked.
 * @param modifier          [Modifier] to be applied to the list item.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun PickPhotoButton(
    requestPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text("Choose a picture") }, leadingContent = {
            Icon(
                painter = painterResource(R.drawable.outline_add_photo_alternate_24),
                contentDescription = "Choose a picture"
            )
        }, colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ), modifier = modifier.clickable {
            requestPickPhoto()
        })
}