package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import dev.joshhalvorson.materialweather.data.models.weather.Forecastday
import dev.joshhalvorson.materialweather.data.models.weather.Hour
import dev.joshhalvorson.materialweather.data.util.temperatureUnitsFlow

@Composable
fun WeekForecastCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    forecast: ForecastResponse?,
    isCurrentHour: (Hour) -> Boolean,
) {
    WeatherCard(modifier = modifier) {
        if (forecast != null) {
            CardContent(
                forecasts = forecast.forecast.forecastday,
                isCurrentHour = isCurrentHour
            )
        } else if (loading) {
            CardContent(
                forecasts = ForecastResponse.LOADING_DATA.forecast.forecastday,
                isCurrentHour = isCurrentHour
            )
        }
    }
}

@Composable
private fun CardContent(
    forecasts: List<Forecastday>,
    isCurrentHour: (Hour) -> Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        forecasts.forEachIndexed { index, forecastDay ->
            val lazyListState = rememberLazyListState()

            val isFirst = index == 0
            var expanded by rememberSaveable { mutableStateOf(isFirst) }
            var indexToScrollTo by rememberSaveable { mutableIntStateOf(-1) }

            LaunchedEffect(Unit) {
                if (isFirst && indexToScrollTo == -1) {
                    indexToScrollTo = forecastDay.hour.indexOfFirst { isCurrentHour(it) }
                    if (indexToScrollTo != -1) {
                        lazyListState.scrollToItem(indexToScrollTo)
                    }
                }
            }

            DayItem(
                lazyListState = lazyListState,
                wrapper = forecastDay,
                expanded = if (isFirst) true else expanded,
                onClicked = { expanded = !expanded },
                isCurrentHour = isCurrentHour,
                isLast = index == forecasts.lastIndex
            )
        }
    }
}

@Composable
private fun DayItem(
    lazyListState: LazyListState,
    wrapper: Forecastday,
    expanded: Boolean,
    onClicked: () -> Unit,
    isCurrentHour: (Hour) -> Boolean,
    isLast: Boolean,
) {
    Row(modifier = Modifier.clickable { onClicked() }) {
        Column(modifier = Modifier.animateContentSize()) {
            DayForecast(dayWeather = wrapper)
            if (expanded) {
                HourlyForecast(
                    state = lazyListState,
                    wrapper = wrapper,
                    isCurrentHour = isCurrentHour
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (!isLast) {
                Divider()
            }
        }
    }
}

@Composable
private fun HourlyForecast(
    state: LazyListState,
    wrapper: Forecastday,
    isCurrentHour: (Hour) -> Boolean
) {
    LazyRow(modifier = Modifier.fillMaxWidth(), state = state) {
        itemsIndexed(items = wrapper.hour, key = { index, item -> item.timeEpoch }) { index, item ->
            HourlyItem(
                forecastProvider = { wrapper },
                hourlyProvider = { item },
                isCurrentHour = isCurrentHour
            )
        }
    }
}

@Composable
private fun HourlyItem(
    forecastProvider: () -> Forecastday,
    hourlyProvider: () -> Hour,
    isCurrentHour: (Hour) -> Boolean,
) {
    val context = LocalContext.current
    val temperatureSetting by context.temperatureUnitsFlow()
        .collectAsStateWithLifecycle(initialValue = "")

    val maxTemp by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> forecastProvider().day.maxtempC.toInt()
                else -> forecastProvider().day.maxtempF.toInt()
            }
        }
    }

    val hourlyTemp by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> hourlyProvider().tempC.toInt()
                else -> hourlyProvider().tempF.toInt()
            }
        }
    }

    val hourlyTempString by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> hourlyProvider().getCurrentTempC()
                else -> hourlyProvider().getCurrentTempF()
            }
        }
    }

    val currentHourModifier = if (isCurrentHour(hourlyProvider())) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = RectangleShape
        )
    } else {
        Modifier
    }

    Column(
        modifier = currentHourModifier.width(80.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.CenterEnd) {
            val textModifier = Modifier.padding(top = 4.dp)

            Text(
                modifier = textModifier,
                text = hourlyProvider().getTimeDisplay(),
                style = MaterialTheme.typography.labelSmall
            )

            // Placeholder
            Text(
                modifier = textModifier
                    .alpha(0f)
                    .semantics { contentDescription = "" },
                text = "12:00 AM",
                style = MaterialTheme.typography.labelSmall
            )
        }

        AsyncImage(
            modifier = Modifier.size(16.dp),
            model = hourlyProvider().condition.getIconUrl(),
            contentDescription = null
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

        Spacer(Modifier.height(4.dp))

        TemperatureBar(
            dailyMaxTemperature = maxTemp,
            hourTemperature = hourlyTemp,
            tempText = {
                Text(
                    text = hourlyTempString,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )
    }
}

@Composable
private fun TemperatureBar(
    dailyMaxTemperature: Int,
    hourTemperature: Int,
    tempText: @Composable () -> Unit,
) {
    val percentToFill by remember {
        derivedStateOf {
            val float = if (dailyMaxTemperature == 0) {
                0f
            } else {
                (hourTemperature.toFloat() / dailyMaxTemperature.toFloat()) * 100f
            }

            if (float >= 100f) {
                1f
            } else if (float != 0f) {
                ".${float.toString().replace(".", "")}".toFloat()
            } else {
                0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(.75f)
            .height(100.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        tempText()

        Box(
            modifier = Modifier
                .fillMaxHeight(percentToFill)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        )
    }
}

@Composable
private fun DayForecast(dayWeather: Forecastday) {
    val context = LocalContext.current
    val temperatureSetting by context.temperatureUnitsFlow()
        .collectAsStateWithLifecycle(initialValue = "")

    val minMaxTemp by remember {
        derivedStateOf {
            when (temperatureSetting) {
                context.getString(R.string.celsius) -> dayWeather.day.getMinMaxTempC()
                else -> dayWeather.day.getMinMaxTempF()
            }
        }
    }

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

        Text(
            modifier = Modifier.defaultMinSize(minWidth = 40.dp),
            text = minMaxTemp
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