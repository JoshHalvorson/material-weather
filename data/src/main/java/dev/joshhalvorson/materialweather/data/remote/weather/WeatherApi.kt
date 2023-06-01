package dev.joshhalvorson.materialweather.data.remote.weather

import dev.joshhalvorson.materialweather.data.models.weather.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    suspend fun getWeather(
        @Query(value = "key", encoded = true)
        key: String,
        @Query(value = "q", encoded = true)
        latLon: String,
        @Query(value = "aqi", encoded = true)
        aqi: String,
        @Query(value = "days", encoded = true)
        days: Int,
        @Query(value = "alerts", encoded = true)
        alerts: String,
    ): Response<ForecastResponse>
}