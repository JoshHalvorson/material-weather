package dev.joshhalvorson.materialweather.ui.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.joshhalvorson.materialweather.data.models.weather.AirQuality
import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import dev.joshhalvorson.materialweather.data.models.weather.Hour
import dev.joshhalvorson.materialweather.data.models.weather.Severity
import dev.joshhalvorson.materialweather.data.models.weather.WeatherAlert
import dev.joshhalvorson.materialweather.data.repository.generativeweatherreport.GenerativeWeatherReportRepository
import dev.joshhalvorson.materialweather.data.repository.weather.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val generativeWeatherReportRepository: GenerativeWeatherReportRepository
) : ViewModel() {
    private val mCurrentWeather = MutableStateFlow<ForecastResponse?>(null)
    val currentWeather = mCurrentWeather.asStateFlow()

    private val mLoading = MutableStateFlow(true)
    val loading = mLoading.asStateFlow()

    private val mLoading2 = MutableStateFlow(true)
    val loading2 = mLoading2.asStateFlow()

    private val mRetrievedWeather = MutableStateFlow(false)
    val retrievedWeather = mRetrievedWeather.asStateFlow()

    private val mRefreshing = MutableStateFlow(false)
    val refreshing = mRefreshing.asStateFlow()

    private val mError = MutableStateFlow(false)
    val error = mError.asStateFlow()

    private val mErrorMessage = MutableStateFlow("")
    val errorMessage = mErrorMessage.asStateFlow()

    private val mCurrentLocation = MutableStateFlow(Pair(Double.MIN_VALUE, Double.MIN_VALUE))
    val currentLocation = mCurrentLocation.asStateFlow()

    private val mClickedAlert = MutableStateFlow<WeatherAlert?>(null)
    val clickedAlert = mClickedAlert.asStateFlow()

    private val mShowAlertInfoDialog = MutableStateFlow(false)
    val showAlertInfoDialog = mShowAlertInfoDialog.asStateFlow()

    private val mClickedAirQuality = MutableStateFlow<AirQuality?>(null)
    val clickedAirQuality = mClickedAirQuality.asStateFlow()

    private val mShowAirQualityInfoDialog = MutableStateFlow(false)
    val showAirQualityInfoDialog = mShowAirQualityInfoDialog.asStateFlow()

    private val mGenerativeWeatherAlert = MutableStateFlow<WeatherAlert?>(null)
    val generativeWeatherAlert = mGenerativeWeatherAlert.asStateFlow()

    fun onAirQualityClicked(airQuality: AirQuality?) = viewModelScope.launch {
        mClickedAirQuality.emit(airQuality)
        mShowAirQualityInfoDialog.emit(true)
    }

    fun onAirQualityInfoDialogClosed() = viewModelScope.launch {
        mShowAirQualityInfoDialog.emit(false)
        mClickedAirQuality.emit(null)
    }

    fun onAlertClicked(weatherAlert: WeatherAlert) = viewModelScope.launch {
        mClickedAlert.emit(weatherAlert)
        mShowAlertInfoDialog.emit(true)
    }

    fun onAlertInfoDialogClosed() = viewModelScope.launch {
        mShowAlertInfoDialog.emit(false)
        mClickedAlert.emit(null)
    }

    private fun getGenerativeAlerts() = viewModelScope.launch {
        val todaysWeather = mCurrentWeather.value?.forecast?.forecastday?.first()?.day
        val tomorrowsWeather = mCurrentWeather.value?.forecast?.forecastday?.get(1)?.day

        if (todaysWeather == null || tomorrowsWeather == null) return@launch

        generativeWeatherReportRepository.getWeatherAlert(todaysWeather, tomorrowsWeather)
            .onStart {
                Log.i("HomeViewModel", "Getting generative alert")

                mLoading2.emit(true)
            }
            .catch {
                Log.e("HomeViewModel", "Error Getting generative alert", it)

                mError.emit(true)
                mLoading2.emit(false)
            }
            .collect { response ->
                mLoading2.emit(false)
                response?.let { text ->
                    mGenerativeWeatherAlert.emit(
                        WeatherAlert(
                            event = "Tomorrow",
                            headline = text,
                            desc = text,
                            severity = Severity.Unknown.name,
                            isGenerative = true
                        )
                    )
                }
            }
    }

    /**
     * Suppressing since this will only get called if we have location permissions
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(locationClient: FusedLocationProviderClient) {
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { currentLocation ->
                if (currentLocation != null) {
                    getCurrentWeather(
                        lat = currentLocation.latitude,
                        lon = currentLocation.longitude
                    )
                } else {
                    viewModelScope.launch {
                        mError.emit(true)
                        mErrorMessage.emit("Error getting location")
                    }
                }
            }
    }

    fun refreshWeather() = viewModelScope.launch {
        mRetrievedWeather.emit(false)
        mLoading.emit(true)
        mLoading2.emit(true)
        // Delay for smoother transition from loading state to retrieved data in the case of a really
        // quick response
        delay(500)

        getCurrentWeather(lat = mCurrentLocation.value.first, lon = mCurrentLocation.value.second)
    }

    fun getCurrentWeather(lat: Double, lon: Double) = viewModelScope.launch {
        mCurrentLocation.emit(Pair(lat, lon))
        weatherRepository.getWeather(lat = lat, lon = lon)
            .onStart {
                Log.i("HomeViewModel", "Getting current weather")
                mLoading.emit(true)
                mCurrentWeather.emit(null)
            }
            .catch {
                mLoading.emit(false)
                mRefreshing.emit(false)
                mError.emit(true)
                mErrorMessage.emit(it.message ?: "No error message")
                Firebase.crashlytics.recordException(it)
            }
            .collect {
                it?.let { weatherResponse ->
                    mCurrentWeather.emit(weatherResponse)
                    getGenerativeAlerts()
                } ?: run {
                    mError.emit(true)
                }
                mLoading.emit(false)
                mRefreshing.emit(false)
                mRetrievedWeather.emit(true)
            }
    }

    fun isCurrentHour(hour: Hour): Boolean {
        return try {
            val localTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(hour.timeEpoch), TimeZone.getDefault().toZoneId()
            )
            localTime.hour == LocalDateTime.now().hour
        } catch (e: DateTimeParseException) {
            false
        }
    }
}