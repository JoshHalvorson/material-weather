package dev.joshhalvorson.materialweather.data.repository.gpt

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.joshhalvorson.materialweather.data.models.gpt.Choice
import dev.joshhalvorson.materialweather.data.models.gpt.GptRequestBody
import dev.joshhalvorson.materialweather.data.models.gpt.GptResponse
import dev.joshhalvorson.materialweather.data.models.weather.Day
import dev.joshhalvorson.materialweather.data.remote.gpt.GptApi
import dev.joshhalvorson.materialweather.data.util.generatedAlertTextFlow
import dev.joshhalvorson.materialweather.data.util.getGenerativeWeatherAlertPrompt
import dev.joshhalvorson.materialweather.data.util.lastGeneratedAlertFlow
import dev.joshhalvorson.materialweather.data.util.storeGeneratedAlertText
import dev.joshhalvorson.materialweather.data.util.storeLastGeneratedAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GptRepository @Inject constructor(
    private val gptApi: GptApi,
    @ApplicationContext
    private val context: Context
) {
    suspend fun getWeatherAlerts(
        todaysWeather: Day,
        tomorrowsWeather: Day,
        temperatureUnit: String,
        unit: String
    ) =
        flow {
            try {
                val lastGptAlert = context.lastGeneratedAlertFlow()
                val gptAlertText = context.generatedAlertTextFlow()
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

                if (!lastGptAlert.firstOrNull().isNullOrEmpty() && lastGptAlertTime.plusHours(3)
                        .isAfter(now)
                ) {
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
                            prompt = getGenerativeWeatherAlertPrompt(
                                tomorrowsWeather = tomorrowsWeather,
                                tempUnit = temperatureUnit,
                                unit = unit
                            )
                        )
                    )

                    response.body()?.let { gptResponse ->
                        Log.i("GptRepository", "$gptResponse")
                        context.storeLastGeneratedAlert(
                            lastGeneratedAlert = System.currentTimeMillis().toString()
                        )
                        context.storeGeneratedAlertText(
                            generatedAlertText = gptResponse.choices.firstOrNull()?.text ?: ""
                        )
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