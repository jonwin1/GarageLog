package se.umu.cs.c22jwt.garagelog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

/**
 * A gradient to protect the status bar when displaying content underneath.
 *
 * https://developer.android.com/develop/ui/compose/system/system-bars#create-translucent
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun StatusBarProtection() {
    val color: Color = MaterialTheme.colorScheme.surfaceContainer
    val height = WindowInsets.systemBars.getTop(LocalDensity.current).times(1.2f)
    val gradient = Brush.verticalGradient(
        colors = listOf(
            color.copy(alpha = .8f), color.copy(alpha = .5f), Color.Transparent
        ), startY = 0f, endY = height
    )

    Canvas(Modifier.fillMaxSize()) {
        drawRect(
            brush = gradient,
            size = Size(size.width, height),
        )
    }
}
