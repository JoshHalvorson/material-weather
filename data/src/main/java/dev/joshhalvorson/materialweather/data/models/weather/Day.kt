package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

data class Day(
    @SerializedName("maxtemp_c")
    val maxtempC: Double,
    @SerializedName("maxtemp_f")
    val maxtempF: Double,
    @SerializedName("mintemp_c")
    val mintempC: Double,
    @SerializedName("mintemp_f")
    val mintempF: Double,
    @SerializedName("avgtemp_c")
    val avgtempC: Double,
    @SerializedName("avgtemp_f")
    val avgtempF: Double,
    @SerializedName("maxwind_mph")
    val maxwindMph: Double,
    @SerializedName("maxwind_kph")
    val maxwindKph: Double,
    @SerializedName("totalprecip_mm")
    val totalprecipMm: Double,
    @SerializedName("totalprecip_in")
    val totalprecipIn: Double,
    @SerializedName("totalsnow_cm")
    val totalsnowCm: Double,
    @SerializedName("avgvis_km")
    val avgvisKm: Double,
    @SerializedName("avgvis_miles")
    val avgvisMiles: Double,
    @SerializedName("avghumidity")
    val avghumidity: Double,
    @SerializedName("daily_will_it_rain")
    val dailyWillItRain: Int,
    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int,
    @SerializedName("daily_will_it_snow")
    val dailyWillItSnow: Int,
    @SerializedName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("uv")
    val uv: Double,
    @SerializedName("air_quality")
    val airQuality: AirQuality
) {
    fun getMinMaxTempF(): String {
        return "${maxtempF.roundToInt()}°/${mintempF.roundToInt()}° "
    }

    fun getMinMaxTempC(): String {
        return "${maxtempC.roundToInt()}°/${mintempC.roundToInt()}° "
    }

    fun getChanceOfRain(): String {
        return "$dailyChanceOfRain%"
    }
}