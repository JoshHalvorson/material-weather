package dev.joshhalvorson.materialweather.data.repository.generativeweatherreport

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.joshhalvorson.materialweather.data.BuildConfig
import dev.joshhalvorson.materialweather.data.models.weather.Day
import dev.joshhalvorson.materialweather.data.util.Key
import dev.joshhalvorson.materialweather.data.util.dataStore
import dev.joshhalvorson.materialweather.data.util.getGenerativeWeatherAlertPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GenerativeWeatherReportRepository @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    // TODO support metric
    suspend fun getWeatherAlert(
        todaysWeather: Day,
        tomorrowsWeather: Day,
        unit: String = "imperial"
    ) = flow {
        try {
            val lastGeneratedAlert = context.dataStore.data.map { preferences ->
                preferences[Key.LAST_GENERATED_ALERT] ?: ""
            }
            val generatedAlertText = context.dataStore.data.map { preferences ->
                preferences[Key.GENERATED_ALERT_TEXT] ?: ""
            }
            val lastGeneratedAlertTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastGeneratedAlert.firstOrNull()?.takeIf { it.isNotEmpty() }
                    ?.toLong() ?: Long.MAX_VALUE),
                ZoneId.systemDefault()
            )
            val now = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis()),
                ZoneId.systemDefault()
            )

            Log.i("GenerativeWeatherReportRepository", "last alert time: $lastGeneratedAlertTime")
            Log.i("GenerativeWeatherReportRepository", "now: $now")

            if (shouldUseCachedAlert(
                    lastGeneratedAlert = lastGeneratedAlert.firstOrNull(),
                    lastGeneratedAlertTime = lastGeneratedAlertTime,
                    now = now
                )
            ) {
                Log.i("GenerativeWeatherReportRepository", "Getting saved generated response")
                emit(generatedAlertText.firstOrNull())
            } else {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-pro",
                    apiKey = BuildConfig.GEMINI_KEY
                )
                val response = generativeModel.generateContent(
                    getGenerativeWeatherAlertPrompt(
                        tomorrowsWeather = tomorrowsWeather,
                        unit = unit
                    )
                )

                response.text?.let { generatedResponse ->
                    Log.i("GenerativeWeatherReportRepository", "$generatedResponse")
                    context.dataStore.edit { settings ->
                        settings[Key.LAST_GENERATED_ALERT] = System.currentTimeMillis().toString()
                        settings[Key.GENERATED_ALERT_TEXT] = generatedResponse
                    }
                    emit(generatedResponse)
                } ?: run {
                    Log.e(
                        "GenerativeWeatherReportRepository",
                        "No response text"
                    )
                    emit(null)
                }
            }
        } catch (e: Exception) {
            Log.e("GenerativeWeatherReportRepository", "Error", e)
            emit(null)
        }
    }.flowOn(Dispatchers.IO)

    private fun shouldUseCachedAlert(
        lastGeneratedAlert: String?,
        lastGeneratedAlertTime: LocalDateTime,
        now: LocalDateTime?
    ) = !lastGeneratedAlert.isNullOrEmpty() && lastGeneratedAlertTime.plusHours(3).isAfter(now)
}