package dev.joshhalvorson.materialweather.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.ui.components.AirQualityCard
import dev.joshhalvorson.materialweather.ui.components.AlertsCard
import dev.joshhalvorson.materialweather.ui.components.AstroCard
import dev.joshhalvorson.materialweather.ui.components.CurrentWeatherCard
import dev.joshhalvorson.materialweather.ui.components.LinkifyText
import dev.joshhalvorson.materialweather.ui.components.PullRefresh
import dev.joshhalvorson.materialweather.ui.components.UvCard
import dev.joshhalvorson.materialweather.ui.components.WeekForecastCard
import dev.joshhalvorson.materialweather.ui.components.rememberPullRefreshState
import dev.joshhalvorson.materialweather.ui.components.weatherPlaceholder
import dev.joshhalvorson.materialweather.ui.theme.card
import dev.joshhalvorson.materialweather.ui.theme.weatherCard
import dev.joshhalvorson.materialweather.ui.viewmodel.HomeViewModel


@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    // State
    val currentWeather by viewModel.currentWeather.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val loadingAlerts by viewModel.loading2.collectAsState()
    val error by viewModel.error.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val refreshing by viewModel.refreshing.collectAsState()
    val retrievedWeather by viewModel.retrievedWeather.collectAsState()
    val showAlertInfoDialog by viewModel.showAlertInfoDialog.collectAsState()
    val clickedAlert by viewModel.clickedAlert.collectAsState()
    val showAirQualityInfoDialog by viewModel.showAirQualityInfoDialog.collectAsState()
    val clickedAirQuality by viewModel.clickedAirQuality.collectAsState()
    val gptAlerts by viewModel.gptWeatherAlerts.collectAsState()

    var hasPermissions by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val swipeRefreshState = rememberPullRefreshState(refreshing)

    val context = LocalContext.current
    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            hasPermissions = it.containsValue(true)
        }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(hasPermissions) {
        if (hasPermissions == true && !retrievedWeather) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.getCurrentWeather(lat = location.latitude, lon = location.longitude)
                } else {
                    viewModel.getCurrentLocation(fusedLocationClient)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                hasPermissions = true
            }

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                hasPermissions = true
            }

            else -> hasPermissions = false
        }
    }

    val requestPermissions = {
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    if (hasPermissions == false) {
        AlertDialog(
            title = {
                Text(
                    text = stringResource(
                        R.string.permissions_dialog_title,
                        stringResource(id = R.string.app_name)
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.permissions_dialog_text,
                        stringResource(id = R.string.app_name)
                    )
                )
            },
            onDismissRequest = { requestPermissions() },
            confirmButton = {
                TextButton(
                    onClick = { requestPermissions() }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            },
        )
    }

    if (showAlertInfoDialog && clickedAlert != null) {
        AlertDialog(
            title = {
                Text(
                    text = clickedAlert?.event ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LinkifyText(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    text = clickedAlert?.desc
                )
            },
            onDismissRequest = { viewModel.onAlertInfoDialogClosed() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onAlertInfoDialogClosed() }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            },
        )
    }

    if (showAirQualityInfoDialog && clickedAirQuality != null) {
        AlertDialog(
            title = {
                Text(
                    text = stringResource(id = R.string.aqi_card_title),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stringResource(R.string.co))
                        Text(text = stringResource(R.string.no2))
                        Text(text = stringResource(R.string.o3))
                        Text(text = stringResource(R.string.so2))
                        Text(text = stringResource(R.string.pm2_5))
                        Text(text = stringResource(R.string.pm10))
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        clickedAirQuality?.let {
                            Text(text = stringResource(R.string.g_m3, it.getCoDisplay()))
                            Text(text = stringResource(R.string.g_m3, it.getNo2Display()))
                            Text(text = stringResource(R.string.g_m3, it.getO3Display()))
                            Text(text = stringResource(R.string.g_m3, it.getSo2Display()))
                            Text(text = stringResource(R.string.g_m3, it.getPm25Display()))
                            Text(text = stringResource(R.string.g_m3, it.getPm10Display()))
                        }
                    }
                }
            },
            onDismissRequest = { viewModel.onAirQualityInfoDialogClosed() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onAirQualityInfoDialogClosed() }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            },
        )
    }

    if (!loading && error) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage)
        }
    }

    PullRefresh(swipeRefreshState = swipeRefreshState, onRefresh = viewModel::refreshWeather) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!error) {
                CurrentWeatherCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .weatherPlaceholder(
                            visible = loading,
                            shape = MaterialTheme.shapes.weatherCard
                        ),
                    currentWeather = currentWeather,
                    loading = loading
                )

                AlertsCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weatherPlaceholder(visible = loadingAlerts),
                    loading = loadingAlerts,
                    alerts = currentWeather?.alerts,
                    dialogVisible = showAlertInfoDialog,
                    gptAlerts = gptAlerts,
                    onAlertClicked = viewModel::onAlertClicked
                )

                WeekForecastCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weatherPlaceholder(visible = loading),
                    forecast = currentWeather,
                    loading = loading
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AirQualityCard(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                            .weatherPlaceholder(visible = loading)
                            .clip(MaterialTheme.shapes.card)
                            .clickable {
                                viewModel.onAirQualityClicked(airQuality = currentWeather?.current?.airQuality)
                            },
                        loading = loading,
                        airQuality = currentWeather?.current?.airQuality
                    )
                    UvCard(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxHeight()
                            .weight(1f)
                            .weatherPlaceholder(visible = loading),
                        loading = loading,
                        uvIndex = currentWeather?.current?.uv
                    )
                }

                AstroCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weatherPlaceholder(visible = loading),
                    loading = loading,
                    astroData = currentWeather?.forecast?.forecastday?.firstOrNull()?.astroData
                )

                // For spacing
                Box {}
            }
        }
    }
}