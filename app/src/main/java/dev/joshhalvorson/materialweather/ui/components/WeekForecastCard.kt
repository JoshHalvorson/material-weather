package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.weather.Day
import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import dev.joshhalvorson.materialweather.data.models.weather.Forecastday
import dev.joshhalvorson.materialweather.data.models.weather.Hour

@Composable
fun WeekForecastCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    forecast: ForecastResponse?
) {
    WeatherCard(modifier = modifier) {
        if (forecast != null) {
            CardContent(forecasts = forecast.forecast.forecastday)
        } else if (loading) {
            CardContent(forecasts = ForecastResponse.LOADING_DATA.forecast.forecastday)
        }
    }
}

@Composable
private fun CardContent(forecasts: List<Forecastday>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        forecasts.forEachIndexed { index, forecastDay ->
            val isFirst = index == 0
            var expanded by rememberSaveable { mutableStateOf(isFirst) }

            DayItem(
                wrapper = forecastDay,
                expanded = if (isFirst) true else expanded,
                onClicked = { expanded = !expanded })
        }
    }
}

@Composable
private fun DayItem(wrapper: Forecastday, expanded: Boolean, onClicked: () -> Unit) {
    Row(modifier = Modifier.clickable { onClicked() }) {
        Column(modifier = Modifier.animateContentSize()) {
            DayForecast(dayWeather = wrapper)
            if (expanded) {
                HourlyForecast(wrapper = wrapper)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HourlyForecast(wrapper: Forecastday) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(250.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = wrapper.hour, key = { it.timeEpoch }) {
            HourlyItem(dayForecastProvider = { wrapper.day }, hourlyProvider = { it })
        }
    }
}

@Composable
private fun HourlyItem(dayForecastProvider: () -> Day, hourlyProvider: () -> Hour) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier.size(16.dp),
            model = hourlyProvider().condition.getIconUrl(),
            contentDescription = null
        )

        // TODO add metric support
        Text(
            text = hourlyProvider().getCurrentTempF(),
            style = MaterialTheme.typography.labelSmall
        )

        Box(
            contentAlignment = Alignment.CenterEnd
        ) {
            PrecipitationChance(chanceOfRain = hourlyProvider().getChanceOfRain(), small = true)

            PrecipitationChance(
                placeholder = true,
                chanceOfRain = "100%",
                small = true
            )
        }

        Box(
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = hourlyProvider().getTimeDisplay(),
                style = MaterialTheme.typography.labelSmall
            )

            // Placeholder
            Text(
                modifier = Modifier
                    .alpha(0f)
                    .semantics { contentDescription = "" },
                text = "12:00 AM",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.width(20.dp))

        // TODO add metric support
        TemperatureBar(
            dailyMaxTemperature = dayForecastProvider().maxtempF.toInt(),
            hourTemperature = hourlyProvider().tempF.toInt()
        )
    }
}

@Composable
private fun TemperatureBar(dailyMaxTemperature: Int, hourTemperature: Int) {
    val percentToFill by remember {
        derivedStateOf {
            val float = if (dailyMaxTemperature == 0) {
                0f
            } else {
                (hourTemperature.toFloat() / dailyMaxTemperature.toFloat()) * 100f
            }

            if (float == 100f) {
                1f
            } else if (float != 0f) {
                ".${float.toString().replace(".", "")}".toFloat()
            } else {
                0f
            }
        }
    }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(color = MaterialTheme.colorScheme.outlineVariant)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(percentToFill)
                .height(10.dp)
                .align(Alignment.CenterEnd)
                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = percentToFill))
        )
    }
}

@Composable
private fun DayForecast(dayWeather: Forecastday) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = dayWeather.getDay(),
            overflow = TextOverflow.Visible,
            maxLines = 1
        )

        PrecipitationChance(chanceOfRain = dayWeather.day.getChanceOfRain())

        dayWeather.day.condition.let {
            Box(
                modifier = Modifier.defaultMinSize(minWidth = 40.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                AsyncImage(
                    modifier = Modifier.size(28.dp),
                    model = it.getIconUrl(),
                    contentDescription = null
                )
            }
        }

        // TODO add metric support
        Text(
            modifier = Modifier.defaultMinSize(minWidth = 40.dp),
            text = dayWeather.day.getMinMaxTempF()
        )
    }
}

@Composable
private fun PrecipitationChance(
    placeholder: Boolean = false,
    chanceOfRain: String,
    small: Boolean = false
) {
    val textModifier = if (placeholder) {
        Modifier.semantics { contentDescription = "" }
    } else {
        Modifier
    }

    Row(
        modifier = if (placeholder) Modifier.alpha(0f) else Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            modifier = Modifier.size(if (!small) 18.dp else 12.dp),
            painter = painterResource(id = R.drawable.baseline_water_drop_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            modifier = Modifier
                .defaultMinSize(minWidth = if (!small) 40.dp else 20.dp)
                .then(textModifier),
            text = chanceOfRain,
            style = if (!small) LocalTextStyle.current else MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
    }
}