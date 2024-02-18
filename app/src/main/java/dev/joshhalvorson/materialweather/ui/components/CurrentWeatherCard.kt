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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import dev.joshhalvorson.materialweather.data.util.getTime
import dev.joshhalvorson.materialweather.data.util.physicalUnitsFlow
import dev.joshhalvorson.materialweather.data.util.temperatureUnitsFlow
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
    val context = LocalContext.current
    val unitSetting by context.physicalUnitsFlow().collectAsStateWithLifecycle(initialValue = "")

    val wind by remember {
        derivedStateOf {
            when (unitSetting) {
                context.getString(R.string.metric) -> currentWeather.current.getWindKph()
                else -> currentWeather.current.getWindMph()
            }
        }
    }

    val visibility by remember {
        derivedStateOf {
            when (unitSetting) {
                context.getString(R.string.metric) -> currentWeather.current.getVisibilityKilometers()
                else -> currentWeather.current.getVisibilityMiles()
            }
        }
    }

    val windString by remember {
        derivedStateOf {
            when (unitSetting) {
                context.getString(R.string.metric) -> R.string.speed_kph_winds_from_direction
                else -> R.string.speed_mph_winds_from_direction
            }
        }
    }

    val visibilityString by remember {
        derivedStateOf {
            when (unitSetting) {
                context.getString(R.string.metric) -> R.string.km_visibility
                else -> R.string.mi_visibility
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        with(currentWeather.current) {
            Text(text = stringResource(R.string.percent_humidity, humidity))
            Text(
                text = stringResource(
                    windString,
                    wind,
                    windDir
                )
            )
            Text(text = stringResource(R.string.percent_cloud_coverage, cloud))
            Text(text = stringResource(visibilityString, visibility))

            currentWeather.forecast.forecastday.firstOrNull()?.astroData?.let { astro ->
                Text(text = stringResource(R.string.time_sunrise, astro.getSunriseDisplay()))
                Text(text = stringResource(R.string.time_sunset, astro.getSunsetDisplay()))
            }
        }
    }
}

@Composable
private fun TempData(currentWeather: ForecastResponse) {
    val context = LocalContext.current
    val todayForecast = currentWeather.forecast.forecastday.firstOrNull()?.day

    val temperatureSetting by context.temperatureUnitsFlow()
        .collectAsStateWithLifecycle(initialValue = "")

    val temp by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> currentWeather.current.getCurrentTempC()
                else -> currentWeather.current.getCurrentTempF()
            }
        }
    }
    val minMaxTemp by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> todayForecast?.getMinMaxTempC()
                else -> todayForecast?.getMinMaxTempF()
            }
        }
    }
    val feelLikeTemp by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> currentWeather.current.getFeelsLikeTempC()
                else -> currentWeather.current.getFeelsLikeTempF()
            }
        }
    }

    Column(
        modifier = Modifier.width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = temp,
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
            Text(
                text = minMaxTemp ?: "",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = stringResource(
                    R.string.feels_like_temp,
                    feelLikeTemp
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}