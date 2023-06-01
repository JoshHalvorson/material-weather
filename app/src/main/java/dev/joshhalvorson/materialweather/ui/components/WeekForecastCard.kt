package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.joshhalvorson.materialweather.R
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
    Column {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = wrapper.hour, key = { it.timeEpoch }) {
                HourlyItem(hourly = it)
            }
        }
    }
}

@Composable
private fun HourlyItem(hourly: Hour) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.inversePrimary
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier.size(16.dp),
                model = hourly.condition.getIconUrl(),
                contentDescription = null
            )

            // TODO add metric support
            Text(
                text = hourly.getCurrentTempF(),
                style = MaterialTheme.typography.labelSmall
            )

            PrecipitationChance(chanceOfRain = hourly.getChanceOfRain(), small = true)

            Text(
                text = hourly.getTimeDisplay(),
                style = MaterialTheme.typography.labelSmall
            )
        }
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
private fun PrecipitationChance(chanceOfRain: String, small: Boolean = false) {
    Row(
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
            modifier = Modifier.defaultMinSize(minWidth = if (!small) 40.dp else 20.dp),
            text = chanceOfRain,
            style = if (!small) LocalTextStyle.current else MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
    }
}