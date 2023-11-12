package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.weather.Alerts
import dev.joshhalvorson.materialweather.data.models.weather.HumidityDifferenceType
import dev.joshhalvorson.materialweather.data.models.weather.NoneDifferenceType
import dev.joshhalvorson.materialweather.data.models.weather.Severity
import dev.joshhalvorson.materialweather.data.models.weather.TempDifferenceType
import dev.joshhalvorson.materialweather.data.models.weather.WeatherAlert
import dev.joshhalvorson.materialweather.ui.theme.card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AlertsCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    alerts: Alerts?,
    gptAlerts: List<WeatherAlert>,
    dialogVisible: Boolean,
    onAlertClicked: (WeatherAlert) -> Unit
) {
    var activeAlert by remember { mutableStateOf<WeatherAlert?>(null) }

    WeatherCard(modifier = modifier
        .clip(MaterialTheme.shapes.card)
        .clickable {
            activeAlert?.let {
                if (it.desc.isNotEmpty()) {
                    onAlertClicked(it)
                }
            }
        }
    ) {
        if (alerts != null) {
            CardContent(
                alerts = alerts,
                dialogVisible = dialogVisible,
                gptAlerts = gptAlerts,
                onActiveAlertChanged = { activeAlert = it })
        } else if (loading) {
            CardContent(
                alerts = Alerts.LOADING_DATA,
                dialogVisible = dialogVisible,
                gptAlerts = gptAlerts,
                onActiveAlertChanged = {})
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardContent(
    alerts: Alerts,
    gptAlerts: List<WeatherAlert>,
    dialogVisible: Boolean,
    onActiveAlertChanged: (WeatherAlert) -> Unit
) {
    val alertsToShow = if (gptAlerts.isEmpty()) alerts.allAlerts else alerts.alert + gptAlerts
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        alertsToShow.size
    }

    LaunchedEffect(pagerState.currentPage, alertsToShow) {
        launch {
            val item = alertsToShow[pagerState.currentPage]
            onActiveAlertChanged(item)
        }

        launch(Dispatchers.IO) {
            // auto scroll
//            while (!dialogVisible) {
//                delay(5000)
//                val nextPage = (pagerState.currentPage + 1).mod(alertsToShow.size)
//                pagerState.animateScrollToPage(page = nextPage)
//            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pager content
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            pageContent = { page ->
                val item = alertsToShow[page]

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = if (Severity.getSeverity(item.severity) == Severity.Unknown) R.drawable.baseline_error_24 else R.drawable.baseline_warning_24),
                        contentDescription = stringResource(R.string.alerts),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    AlertText(alert = item)
                }
            }
        )

        // Page dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(alertsToShow.size) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.inversePrimary
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }

    }
}

@Composable
private fun AlertText(alert: WeatherAlert) {
    val itemText = when (val type = alert.differenceType) {
        is TempDifferenceType.Higher -> {
            stringResource(R.string.temp_tomorrow_hotter, type.difference)
        }

        is TempDifferenceType.Lower -> {
            stringResource(R.string.temp_tomorrow_cooler, type.difference)
        }

        is HumidityDifferenceType.Higher -> {
            stringResource(R.string.humidty_tomorrow_higher, type.difference)
        }

        is HumidityDifferenceType.Lower -> {
            stringResource(R.string.humidty_tomorrow_lower, type.difference)
        }

        is NoneDifferenceType -> {
            stringResource(R.string.no_alerts)
        }

        else -> {
            alert.event
        }
    }

    Text(text = itemText)
    Text(
        text = alert.headline,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}