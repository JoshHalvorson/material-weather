package dev.joshhalvorson.materialweather.data.repository.gpt

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.joshhalvorson.materialweather.data.models.gpt.Choice
import dev.joshhalvorson.materialweather.data.models.gpt.GptRequestBody
import dev.joshhalvorson.materialweather.data.models.gpt.GptResponse
import dev.joshhalvorson.materialweather.data.models.weather.Day
import dev.joshhalvorson.materialweather.data.remote.gpt.GptApi
import dev.joshhalvorson.materialweather.data.util.Key
import dev.joshhalvorson.materialweather.data.util.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GptRepository @Inject constructor(
    private val gptApi: GptApi,
    @ApplicationContext
    private val context: Context
) {
    // TODO support metric
    suspend fun getWeatherAlerts(
        todaysWeather: Day,
        tomorrowsWeather: Day,
        unit: String = "imperial"
    ) =
        flow {
            try {
                val lastGptAlert = context.dataStore.data.map { preferences ->
                    preferences[Key.LAST_GPT_ALERT] ?: ""
                }
                val gptAlertText = context.dataStore.data.map { preferences ->
                    preferences[Key.GPT_ALERT_TEXT] ?: ""
                }
                val lastGptAlertTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(lastGptAlert.firstOrNull()?.takeIf { it.isNotEmpty() }
                        ?.toLong() ?: Long.MAX_VALUE),
                    ZoneId.systemDefault()
                )
                val now = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis()),
                    ZoneId.systemDefault()
                )

                Log.i("GptRepository", "last alert time: $lastGptAlertTime")
                Log.i("GptRepository", "now: $now")

                if (!lastGptAlert.firstOrNull().isNullOrEmpty() && lastGptAlertTime.plusHours(3).isAfter(now)) {
                    Log.i("GptRepository", "Getting saved GPT response")
                    emit(
                        GptResponse(
                            choices = listOf(
                                Choice(
                                    text = gptAlertText.firstOrNull() ?: ""
                                )
                            )
                        )
                    )
                } else {
                    val response = gptApi.getWeatherAlerts(
                        requestBody = GptRequestBody(
                            prompt = "Can you give me an interesting piece of information in tomorrows weather ($tomorrowsWeather) in $unit units, ignoring the high and low temperatures, rain, and snow, phrased as a sentence. Also please round any number with a decimal to the nearest whole number"
                        )
                    )

                    response.body()?.let { gptResponse ->
                        Log.i("GptRepository", "$gptResponse")
                        context.dataStore.edit { settings ->
                            settings[Key.LAST_GPT_ALERT] = System.currentTimeMillis().toString()
                            settings[Key.GPT_ALERT_TEXT] =
                                gptResponse.choices.firstOrNull()?.text ?: ""
                        }
                        emit(gptResponse)
                    } ?: run {
                        Log.e(
                            "GptRepository",
                            "Body null: ${response.code()} ${response.errorBody()?.string()}"
                        )
                        emit(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("GptRepository", "Error", e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
}