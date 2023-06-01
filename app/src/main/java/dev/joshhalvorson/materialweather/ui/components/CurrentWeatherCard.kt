package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import dev.joshhalvorson.materialweather.data.util.getTime
import dev.joshhalvorson.materialweather.ui.theme.weatherCard

@Composable
fun CurrentWeatherCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    currentWeather: ForecastResponse?
) {
    WeatherCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.weatherCard
    ) {
        if (currentWeather != null) {
            CardContent(currentWeather = currentWeather)
        } else if (loading) {
            CardContent(currentWeather = ForecastResponse.LOADING_DATA)
        }
    }
}

@Composable
private fun CardContent(currentWeather: ForecastResponse, onSettingsClicked: () -> Unit = {}) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TempData(currentWeather = currentWeather)
            MiscWeatherData(currentWeather = currentWeather)
        }

        Timestamp(currentWeather = currentWeather)

        Spacer(modifier = Modifier.heightIn(8.dp))
    }
}

@Composable
private fun Timestamp(currentWeather: ForecastResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = stringResource(
                R.string.weather_at_timestamp,
                getTime(currentWeather.current.lastUpdatedEpoch)
            ),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun MiscWeatherData(currentWeather: ForecastResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        with(currentWeather.current) {
            Text(text = stringResource(R.string.percent_humidity, humidity))
            // TODO add metric support
            Text(
                text = stringResource(
                    R.string.speed_mph_winds_from_direction,
                    getWindMph(),
                    windDir
                )
            )
            Text(text = stringResource(R.string.percent_cloud_coverage, cloud))
            // TODO add metric support
            Text(text = stringResource(R.string.mi_visibility, getVisibilityMiles()))

            currentWeather.forecast.forecastday.firstOrNull()?.astroData?.let { astro ->
                Text(text = stringResource(R.string.time_sunrise, astro.getSunriseDisplay()))
                Text(text = stringResource(R.string.time_sunset, astro.getSunsetDisplay()))
            }
        }
    }
}

@Composable
private fun TempData(currentWeather: ForecastResponse) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TODO add metric support
        Text(
            text = currentWeather.current.getCurrentTempF(),
            style = MaterialTheme.typography.displayLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = currentWeather.current.condition.text,
                style = MaterialTheme.typography.headlineSmall
            )
            AsyncImage(
                modifier = Modifier.size(36.dp),
                model = currentWeather.current.condition.getIconUrl(),
                contentDescription = null
            )
        }

        Text(
            text = currentWeather.location.name,
            style = MaterialTheme.typography.headlineSmall
        )

        Row(verticalAlignment = Alignment.Top) {
            val todayForecast = currentWeather.forecast.forecastday.firstOrNull()?.day
            // TODO add metric support
            Text(
                text = "${todayForecast?.getMinMaxTempF()}",
                style = MaterialTheme.typography.labelMedium
            )
            // TODO add metric support
            Text(
                text = stringResource(
                    R.string.feels_like_temp,
                    currentWeather.current.getFeelsLikeTempF()
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}