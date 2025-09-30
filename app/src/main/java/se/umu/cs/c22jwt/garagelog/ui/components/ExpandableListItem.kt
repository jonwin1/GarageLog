package se.umu.cs.c22jwt.garagelog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A list item that expands when pressed to show more content.
 *
 * @param headlineContent   The headline content of the list item.
 * @param expandedContent   The content displayed when expanded.
 * @param modifier          [Modifier] to be applied to the list item.
 * @param overlineContent   The content displayed above the headline content.
 * @param supportingContent The supporting content of the list item.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun ExpandableListItem(
    headlineContent: @Composable (() -> Unit),
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column {
        ListItem(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            trailingContent = {
                if (expanded) {
                    Icon(Icons.Default.KeyboardArrowUp, "Collapse content")
                } else {
                    Icon(Icons.Default.KeyboardArrowDown, "Expand content")
                }
            },
            modifier = modifier.clickable { expanded = !expanded })

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                expandedContent()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ExpandableListItemPreview() {
    ExpandableListItem(
        headlineContent = { Text("Headline") },
        overlineContent = { Text("Overline") },
        supportingContent = { Text("Supporting") },
        expandedContent = { Text("Expanded") })
}