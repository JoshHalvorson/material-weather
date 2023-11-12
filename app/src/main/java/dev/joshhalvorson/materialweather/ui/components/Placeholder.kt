package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import dev.joshhalvorson.materialweather.ui.theme.card

@Composable
fun Modifier.weatherPlaceholder(
    visible: Boolean,
    shape: CornerBasedShape = MaterialTheme.shapes.card
): Modifier = composed {
    this.placeholder(
        visible = visible,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .35f),
        highlight = PlaceholderHighlight.fade(),
        shape = shape
    )
}