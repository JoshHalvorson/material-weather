package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName

data class Alerts(
    @SerializedName("alert")
    val alert: List<WeatherAlert> = emptyList(),
    private val appAlerts: List<WeatherAlert>? = null
) {
    companion object {
        val LOADING_DATA = Alerts(
            alert = listOf(
                WeatherAlert(
                    event = "Air Quality Alert",
                    severity = Severity.Moderate.name
                )
            )
        )
    }

    val allAlerts = (alert + (appAlerts ?: emptyList())).distinctBy {
        listOf(
            it.headline,
            it.msgtype,
            it.severity,
            it.urgency,
            it.areas,
            it.category,
            it.certainty,
            it.event,
            it.note,
            it.effective,
            it.expires,
            it.desc,
            it.instruction
        )
    }
}

data class WeatherAlert(
    val headline: String = "",
    val msgtype: String = "",
    val severity: String,
    val urgency: String = "",
    val areas: String = "",
    val category: String = "",
    val certainty: String = "",
    val event: String,
    val note: String = "",
    val effective: String = "",
    val expires: String = "",
    val desc: String = "",
    val instruction: String = "",
    val differenceType: DifferenceType? = null,
    val isGenerative: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeatherAlert

        if (headline != other.headline) return false
        if (msgtype != other.msgtype) return false
        if (severity != other.severity) return false
        if (urgency != other.urgency) return false
        if (areas != other.areas) return false
        if (category != other.category) return false
        if (certainty != other.certainty) return false
        if (event != other.event) return false
        if (note != other.note) return false
        if (effective != other.effective) return false
        if (expires != other.expires) return false
        if (desc != other.desc) return false
        if (instruction != other.instruction) return false
        return differenceType == other.differenceType
    }

    override fun hashCode(): Int {
        var result = headline.hashCode()
        result = 31 * result + msgtype.hashCode()
        result = 31 * result + severity.hashCode()
        result = 31 * result + urgency.hashCode()
        result = 31 * result + areas.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + certainty.hashCode()
        result = 31 * result + event.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + effective.hashCode()
        result = 31 * result + expires.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + instruction.hashCode()
        result = 31 * result + (differenceType?.hashCode() ?: 0)
        return result
    }
}

enum class Severity {
    Moderate,
    Severe,
    Unknown;

    companion object {
        fun getSeverity(severity: String) =
            values().firstOrNull { it.name.equals(severity, true) } ?: Unknown
    }
}

interface DifferenceType

object NoneDifferenceType : DifferenceType

sealed class TempDifferenceType(val difference: Int) : DifferenceType {
    data class Lower(val amount: Int) : TempDifferenceType(difference = amount)
    data class Higher(val amount: Int) : TempDifferenceType(difference = amount)
}

sealed class HumidityDifferenceType(val difference: Int) : DifferenceType {
    data class Lower(val amount: Int) : HumidityDifferenceType(difference = amount)
    data class Higher(val amount: Int) : HumidityDifferenceType(difference = amount)
}