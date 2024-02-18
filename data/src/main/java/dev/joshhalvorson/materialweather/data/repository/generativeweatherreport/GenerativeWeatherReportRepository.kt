package dev.joshhalvorson.materialweather.data.repository.generativeweatherreport

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.joshhalvorson.materialweather.data.BuildConfig
import dev.joshhalvorson.materialweather.data.models.weather.Day
import dev.joshhalvorson.materialweather.data.util.generatedAlertTextFlow
import dev.joshhalvorson.materialweather.data.util.getGenerativeWeatherAlertPrompt
import dev.joshhalvorson.materialweather.data.util.lastGeneratedAlertFlow
import dev.joshhalvorson.materialweather.data.util.storeGeneratedAlertText
import dev.joshhalvorson.materialweather.data.util.storeLastGeneratedAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GenerativeWeatherReportRepository @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    suspend fun getWeatherAlert(
        todaysWeather: Day,
        tomorrowsWeather: Day,
        temperatureUnit: String,
        unit: String,
    ): Flow<String?> = flow {
        try {
            val lastGeneratedAlert = context.lastGeneratedAlertFlow().first()
            val generatedAlertText = context.generatedAlertTextFlow().first()
            val lastGeneratedAlertTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastGeneratedAlert.takeIf { it.isNotEmpty() }?.toLong() ?: Long.MAX_VALUE),
                ZoneId.systemDefault()
            )
            val now = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis()),
                ZoneId.systemDefault()
            )

            Log.i("GenerativeWeatherReportRepository", "last alert time: $lastGeneratedAlertTime")
            Log.i("GenerativeWeatherReportRepository", "now: $now")

            if (shouldUseCachedAlert(
                    lastGeneratedAlert = lastGeneratedAlert,
                    lastGeneratedAlertTime = lastGeneratedAlertTime,
                    now = now
                )
            ) {
                Log.i("GenerativeWeatherReportRepository", "Getting saved generated response")
                emit(generatedAlertText)
            } else {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-pro",
                    apiKey = BuildConfig.GEMINI_KEY
                )
                val response = generativeModel.generateContent(
                    getGenerativeWeatherAlertPrompt(
                        tomorrowsWeather = tomorrowsWeather,
                        tempUnit = temperatureUnit,
                        unit = unit
                    )
                )

                response.text?.let { generatedResponse ->
                    Log.i("GenerativeWeatherReportRepository", "$generatedResponse")

                    context.storeLastGeneratedAlert(
                        lastGeneratedAlert = System.currentTimeMillis().toString()
                    )
                    context.storeGeneratedAlertText(generatedAlertText = generatedResponse)

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