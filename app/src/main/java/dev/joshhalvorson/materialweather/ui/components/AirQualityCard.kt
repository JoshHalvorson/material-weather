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
import dev.joshhalvorson.materialweather.data.models.weather.AirQuality
import dev.joshhalvorson.materialweather.data.models.weather.Quality

@Composable
fun AirQualityCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    airQuality: AirQuality?
) {
    WeatherCard(modifier = modifier) {
        if (airQuality != null) {
            CardContent(airQuality = airQuality)
        } else if (loading) {
            CardContent(airQuality = AirQuality.LOADING_DATA)
        }
    }
}

@Composable
private fun CardContent(airQuality: AirQuality) {
    val qualityString = when (Quality.getQuality(quality = airQuality.usEpaIndex)) {
        Quality.Good -> stringResource(R.string.aqi_good)
        Quality.Moderate -> stringResource(R.string.aqi_moderate)
        Quality.UnhealthyForSensitiveGroup -> stringResource(R.string.aqi_unhealthy_for_sensitive_group)
        Quality.Unhealthy -> stringResource(R.string.aqi_unhealthy)
        Quality.VeryUnhealthy -> stringResource(R.string.aqi_very_unhealthy)
        Quality.Hazardous -> stringResource(R.string.aqi_hazardous)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.aqi_card_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(text = qualityString, style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.aqi_us_epa_standard, airQuality.usEpaIndex),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )

        // TODO view more for the breakdown
    }
}