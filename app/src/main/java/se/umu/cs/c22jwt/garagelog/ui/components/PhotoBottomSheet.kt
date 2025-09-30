package se.umu.cs.c22jwt.garagelog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet for choosing to take a new picture, pick an existing picture, or remove the loaded picture.
 *
 * @param onDismiss         Called when dismissing the sheet.
 * @param requestTakePhoto  Called to request a photo to be taken.
 * @param requestPickPhoto  Called to request a photo to be picked.
 * @param onRemove          Called to remove an existing image.
 * @param bottomSheetState  The state of the bottom sheet.
 * @param modifier          [Modifier] to apply.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoBottomSheet(
    onDismiss: () -> Unit,
    requestTakePhoto: () -> Unit,
    requestPickPhoto: () -> Unit,
    onRemove: (() -> Unit)?,
    bottomSheetState: SheetState,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = bottomSheetState, modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            TakePhotoButton(
                requestTakePhoto = {
                    requestTakePhoto()
                    onDismiss()
                })

            PickPhotoButton(
                requestPickPhoto = {
                    requestPickPhoto()
                    onDismiss()
                })

            if (onRemove != null) {
                ListItem(
                    headlineContent = { Text("Remove picture") }, leadingContent = {
                        Icon(
                            Icons.Default.Delete, contentDescription = "Remove picture"
                        )
                    }, colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ), modifier = Modifier.clickable { onRemove(); onDismiss() })
            }
        }
    }
}
