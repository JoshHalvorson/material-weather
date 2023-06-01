package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

data class Hour(
    @SerializedName("time_epoch")
    val timeEpoch: Long,
    @SerializedName("time")
    val time: String,
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("temp_f")
    val tempF: Double,
    @SerializedName("is_day")
    val isDay: Int,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("wind_mph")
    val windMph: Double,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("wind_degree")
    val windDegree: Int,
    @SerializedName("wind_dir")
    val windDir: String,
    @SerializedName("pressure_mb")
    val pressureMb: Double,
    @SerializedName("pressure_in")
    val pressureIn: Double,
    @SerializedName("precip_mm")
    val precipMm: Double,
    @SerializedName("precip_in")
    val precipIn: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("cloud")
    val cloud: Int,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("feelslike_f")
    val feelslikeF: Double,
    @SerializedName("windchill_c")
    val windchillC: Double,
    @SerializedName("windchill_f")
    val windchillF: Double,
    @SerializedName("heatindex_c")
    val heatindexC: Double,
    @SerializedName("heatindex_f")
    val heatindexF: Double,
    @SerializedName("dewpoint_c")
    val dewpointC: Double,
    @SerializedName("dewpoint_f")
    val dewpointF: Double,
    @SerializedName("will_it_rain")
    val willItRain: Int,
    @SerializedName("chance_of_rain")
    val chanceOfRain: Int,
    @SerializedName("will_it_snow")
    val willItSnow: Int,
    @SerializedName("chance_of_snow")
    val chanceOfSnow: Int,
    @SerializedName("vis_km")
    val visKm: Double,
    @SerializedName("vis_miles")
    val visMiles: Double,
    @SerializedName("gust_mph")
    val gustMph: Double,
    @SerializedName("gust_kph")
    val gustKph: Double,
    @SerializedName("uv")
    val uv: Double,
    @SerializedName("air_quality")
    val airQuality: AirQuality
) {
    fun getCurrentTempF(): String {
        return "${tempF.roundToInt()}°"
    }

    fun getCurrentTempC(): String {
        return "${tempC.roundToInt()}°"
    }

    fun getChanceOfRain(): String {
        return "$chanceOfRain%"
    }

    fun getTimeDisplay(): String {
        return dev.joshhalvorson.materialweather.data.util.getTime(timeEpoch)
    }
}