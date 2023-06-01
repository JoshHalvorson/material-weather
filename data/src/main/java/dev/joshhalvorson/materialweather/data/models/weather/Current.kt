package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

data class Current(
    @SerializedName("last_updated_epoch")
    val lastUpdatedEpoch: Long = 0L,
    @SerializedName("last_updated")
    val lastUpdated: String,
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
    val windDir: String = "",
    @SerializedName("pressure_mb")
    val pressureMb: Double,
    @SerializedName("pressure_in")
    val pressureIn: Double,
    @SerializedName("precip_mm")
    val precipMm: Double,
    @SerializedName("precip_in")
    val precipIn: Double,
    @SerializedName("humidity")
    val humidity: Int = 0,
    @SerializedName("cloud")
    val cloud: Int = 0,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("feelslike_f")
    val feelslikeF: Double,
    @SerializedName("vis_km")
    val visKm: Double,
    @SerializedName("vis_miles")
    val visMiles: Double,
    @SerializedName("uv")
    val uv: Double,
    @SerializedName("gust_mph")
    val gustMph: Double,
    @SerializedName("gust_kph")
    val gustKph: Double,
    @SerializedName("air_quality")
    val airQuality: AirQuality
) {
    fun getRainInches(): String {
        return "$precipIn"
    }

    fun getRainMm(): String {
        return "$precipMm"
    }

    fun getVisibilityMiles(): String {
        return "$visMiles"
    }

    fun getVisibilityKilometers(): String {
        return "$visKm"
    }

    fun getWindMph(): String {
        return "${windMph.roundToInt()}"
    }

    fun getWindKph(): String {
        return "${windKph.roundToInt()}"
    }

    fun getCurrentTempF(): String {
        return "${tempF.roundToInt()}°"
    }

    fun getCurrentTempC(): String {
        return "${tempC.roundToInt()}°"
    }

    fun getFeelsLikeTempF(): String {
        return "${feelslikeF.roundToInt()}°"
    }

    fun getFeelsLikeTempC(): String {
        return "${feelslikeC.roundToInt()}°"
    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}