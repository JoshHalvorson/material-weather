package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName

data class Condition(
    @SerializedName("text")
    val text: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("code")
    val code: Int
) {
    fun getIconUrl(): String {
        return "https:$icon"
    }
}