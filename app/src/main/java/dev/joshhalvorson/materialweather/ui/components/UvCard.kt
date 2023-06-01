package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.joshhalvorson.materialweather.R

@Composable
fun UvCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    uvIndex: Double?
) {
    WeatherCard(modifier = modifier) {
        if (uvIndex != null) {
            CardContent(uvIndex = uvIndex)
        } else if (loading) {
            CardContent(uvIndex = 0.0)
        }
    }
}

@Composable
private fun CardContent(uvIndex: Double) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.uv_index_card_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(text = "$uvIndex", style = MaterialTheme.typography.titleLarge)
    }
}
