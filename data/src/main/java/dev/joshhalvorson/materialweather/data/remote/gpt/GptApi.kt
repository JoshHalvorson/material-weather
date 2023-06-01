package dev.joshhalvorson.materialweather.data.remote.gpt

import dev.joshhalvorson.materialweather.data.BuildConfig
import dev.joshhalvorson.materialweather.data.models.gpt.GptRequestBody
import dev.joshhalvorson.materialweather.data.models.gpt.GptResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GptApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.GPT_KEY}"
    )
    @POST("completions")
    suspend fun getWeatherAlerts(
        @Body
        requestBody: GptRequestBody
    ): Response<GptResponse>
}