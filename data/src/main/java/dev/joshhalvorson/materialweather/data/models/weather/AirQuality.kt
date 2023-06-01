package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat

data class AirQuality(
    @SerializedName("co")
    val co: Double,
    @SerializedName("no2")
    val no2: Double,
    @SerializedName("o3")
    val o3: Double,
    @SerializedName("so2")
    val so2: Double,
    @SerializedName("pm2_5")
    val pm25: Double,
    @SerializedName("pm10")
    val pm10: Double,
    @SerializedName("us-epa-index")
    val usEpaIndex: Int,
    @SerializedName("gb-defra-index")
    val gbDefraIndex: Int
) {
    companion object {
        val LOADING_DATA = AirQuality(
            usEpaIndex = 1,
            gbDefraIndex = 1,
            co = 0.0,
            no2 = 0.0,
            o3 = 0.0,
            so2 = 0.0,
            pm25 = 0.0,
            pm10 = 0.0
        )

        private val format = DecimalFormat("0.00")
    }

    fun getCoDisplay(): String {
        return format.format(co)
    }

    fun getNo2Display(): String {
        return format.format(no2)
    }

    fun getO3Display(): String {
        return format.format(o3)
    }

    fun getSo2Display(): String {
        return format.format(so2)
    }

    fun getPm25Display(): String {
        return format.format(pm25)
    }

    fun getPm10Display(): String {
        return format.format(pm10)
    }
}

enum class Quality {
    Good,
    Moderate,
    UnhealthyForSensitiveGroup,
    Unhealthy,
    VeryUnhealthy,
    Hazardous;

    companion object {
        fun getQuality(quality: Int) = values()[quality - 1]
    }
}