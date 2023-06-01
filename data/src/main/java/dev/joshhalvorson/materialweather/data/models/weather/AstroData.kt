package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName

data class AstroData(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String,
    @SerializedName("moonrise")
    val moonrise: String,
    @SerializedName("moonset")
    val moonset: String,
    @SerializedName("moon_phase")
    val moonPhase: String,
    @SerializedName("moon_illumination")
    val moonIllumination: String,
    @SerializedName("is_moon_up")
    val isMoonUp: Int,
    @SerializedName("is_sun_up")
    val isSunUp: Int
) {
    companion object {
        val LOADING_DATA = AstroData(
            sunrise = "1:00 AM",
            sunset = "1:00 AM",
            moonrise = "1:00 AM",
            moonset = "1:00 AM",
            moonPhase = "Waxing Gibbous",
            isMoonUp = 1,
            isSunUp = 1,
            moonIllumination = "50"
        )
    }

    fun getSunriseDisplay(): String {
        return sunrise.removePrefix("0")
    }

    fun getSunsetDisplay(): String {
        return sunset.removePrefix("0")
    }

    fun getMoonriseDisplay(): String {
        return moonrise.removePrefix("0")
    }

    fun getMoonsetDisplay(): String {
        return moonset.removePrefix("0")
    }
}

enum class MoonPhase {
    THIRD_QUARTER,
    WANING_GIBBOUS,
    FULL_MOON,
    WAXING_GIBBOUS,
    FIRST_QUARTER,
    WAXING_CRESCENT,
    NEW_MOON,
    WANING_CRESCENT;

    companion object {
        fun getPhase(phaseString: String) =
            values().firstOrNull { phaseString.equals(it.name.replace("_", " "), true) }
                ?: FULL_MOON
    }
}