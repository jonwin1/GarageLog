package se.umu.cs.c22jwt.garagelog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * A dialog for confirming whether to really delete something.
 *
 * @param onDismiss         Called when closing the dialog.
 * @param onDelete          Called when delete button is pressed.
 * @param modifier          [Modifier] to be applied to the list item.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun DeleteDialog(onDismiss: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(onClick = { onDelete(); onDismiss() }) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        icon = { Icon(Icons.Default.Delete, contentDescription = "Trashcan") },
        title = { Text("Permanently delete?") },
        modifier = modifier
    )
}

@Composable
@Preview
fun DeleteDialogPreview() {
    DeleteDialog(onDismiss = {}, onDelete = {})
}
