package dev.joshhalvorson.materialweather.data.repository.weather

import android.util.Log
import dev.joshhalvorson.materialweather.data.BuildConfig
import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import dev.joshhalvorson.materialweather.data.models.weather.HumidityDifferenceType
import dev.joshhalvorson.materialweather.data.models.weather.NoneDifferenceType
import dev.joshhalvorson.materialweather.data.models.weather.Severity
import dev.joshhalvorson.materialweather.data.models.weather.TempDifferenceType
import dev.joshhalvorson.materialweather.data.models.weather.WeatherAlert
import dev.joshhalvorson.materialweather.data.remote.weather.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {
    fun getWeather(lat: Double, lon: Double): Flow<ForecastResponse?> = flow {
        try {
            val response = weatherApi.getWeather(
                key = BuildConfig.API_KEY,
                latLon = "$lat,$lon",
                aqi = "yes",
                days = 5,
                alerts = "yes"
            )
            response.body()?.let { forecastResponse ->
                emit(
                    forecastResponse.copy(
                        alerts = forecastResponse.alerts.copy(
                            appAlerts = constructWeatherAlerts(forecastResponse)
                        )
                    )
                )
            } ?: run {
                Log.e("WeatherRepository", "Error getting forecast")
                throw Exception("Error getting forecast")
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error getting forecast", e)
            throw Exception(e)
        }
    }.flowOn(Dispatchers.IO)

    private fun constructWeatherAlerts(forecastResponse: ForecastResponse): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()

        alerts.getTempAlerts(forecastResponse)
        alerts.getHumidityAlerts(forecastResponse)

        if (alerts.isEmpty()) {
            alerts.add(WeatherAlert(event = "", severity = "", differenceType = NoneDifferenceType))
        }

        return alerts
    }

    private fun MutableList<WeatherAlert>.getHumidityAlerts(forecastResponse: ForecastResponse) {
        val todaysAverageHumidity =
            forecastResponse.forecast.forecastday.firstOrNull()?.day?.avghumidity
        val tomorrowsAverageHumidity =
            forecastResponse.forecast.forecastday.getOrNull(1)?.day?.avghumidity

        if (todaysAverageHumidity == null || tomorrowsAverageHumidity == null) return

        val difference = (todaysAverageHumidity - tomorrowsAverageHumidity).roundToInt()

        if (difference == 0) return

        val todayHigher = difference > 0
        if (todayHigher) {
            add(
                WeatherAlert(
                    event = "",
                    severity = Severity.Unknown.name,
                    differenceType = HumidityDifferenceType.Lower(amount = difference)
                )
            )
        } else {
            add(
                WeatherAlert(
                    event = "",
                    severity = Severity.Unknown.name,
                    differenceType = HumidityDifferenceType.Higher(amount = (difference * -1))
                )
            )
        }
    }

    private fun MutableList<WeatherAlert>.getTempAlerts(forecastResponse: ForecastResponse) {
        // TODO add metric support
        val todaysAverageTemp = forecastResponse.forecast.forecastday.firstOrNull()?.day?.avgtempF
        val tomorrowsAverageTemp = forecastResponse.forecast.forecastday.getOrNull(1)?.day?.avgtempF

        if (todaysAverageTemp == null || tomorrowsAverageTemp == null) return

        val difference = (todaysAverageTemp - tomorrowsAverageTemp).roundToInt()


        if (difference == 0) return


        val todayHigher = difference > 0
        if (todayHigher) {
            add(
                WeatherAlert(
                    event = "",
                    severity = Severity.Unknown.name,
                    differenceType = TempDifferenceType.Lower(amount = difference)
                )
            )
        } else {
            add(
                WeatherAlert(
                    event = "",
                    severity = Severity.Unknown.name,
                    differenceType = TempDifferenceType.Higher(amount = (difference * -1))
                )
            )
        }
    }
}