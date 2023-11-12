package dev.joshhalvorson.materialweather.ui.widget.currentweather

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import dev.joshhalvorson.materialweather.MainActivity
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.repository.weather.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CurrentWeatherWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) = goAsync {
        appWidgetIds.forEach { appWidgetId ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        this.launch {
                             weatherRepository.getWeather(
                                lat = location.latitude, lon = location.longitude
                            ).collectLatest {
                                it?.let { forecastResponse ->
                                    val pendingIntent = PendingIntent.getActivity(
                                        context,
                                        0,
                                        Intent(context, MainActivity::class.java),
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )

                                    val views = RemoteViews(
                                        context.packageName,
                                        R.layout.current_weather_provider_layout
                                    ).apply {
                                        setOnClickPendingIntent(R.id.root, pendingIntent)
                                        val currentTemp = forecastResponse.current.getCurrentTempF()

                                        setTextViewText(R.id.currentTempTextView, currentTemp)
                                    }
                                    appWidgetManager.updateAppWidget(appWidgetId, views)
                                }
                            }
                        }
                    }
                }

            } else {

            }
        }
    }
}

fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob()).launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}