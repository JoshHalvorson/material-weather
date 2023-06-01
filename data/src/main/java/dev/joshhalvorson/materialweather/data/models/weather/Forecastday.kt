package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName
import dev.joshhalvorson.materialweather.data.util.getTimeString

data class Forecastday(
    @SerializedName("date")
    val date: String,
    @SerializedName("date_epoch")
    val dateEpoch: Long,
    @SerializedName("day")
    val day: Day,
    @SerializedName("astro")
    val astroData: AstroData,
    @SerializedName("hour")
    val hour: List<Hour>
) {
    fun getDay(): String {
        return getTimeString(dateEpoch).split(",")[0]
    }
}