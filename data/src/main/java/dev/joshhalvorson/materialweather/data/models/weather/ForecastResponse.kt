package dev.joshhalvorson.materialweather.data.models.weather

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val current: Current,
    @SerializedName("forecast")
    val forecast: Forecast,
    @SerializedName("alerts")
    val alerts: Alerts,
) {
    companion object {
        private val condition = Condition(
            text = "Cloudy",
            icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
            code = 0
        )
        private val airQuality = AirQuality(
            co = 0.0,
            no2 = 0.0,
            o3 = 0.0,
            so2 = 0.0,
            pm25 = 0.0,
            pm10 = 0.0,
            usEpaIndex = 1,
            gbDefraIndex = 1
        )
        private val forecastDay = Forecastday(
            date = "",
            dateEpoch = 0,
            astroData = AstroData(
                sunrise = "1:00 AM",
                sunset = "1:00 AM",
                moonrise = "1:00 AM",
                moonset = "1:00 AM",
                moonPhase = "Waxing Gibbous",
                moonIllumination = "50",
                isMoonUp = 1,
                isSunUp = 1
            ),
            day = Day(
                maxtempF = 70.0,
                mintempF = 50.0,
                maxtempC = 70.0,
                mintempC = 50.0,
                avgtempC = 50.0,
                avgtempF = 50.0,
                maxwindMph = 2.0,
                maxwindKph = 2.0,
                totalprecipMm = 2.0,
                totalprecipIn = 2.0,
                totalsnowCm = 2.0,
                avgvisKm = 2.0,
                avgvisMiles = 6.0,
                avghumidity = 20.0,
                dailyWillItRain = 1,
                dailyWillItSnow = 1,
                dailyChanceOfRain = 5,
                dailyChanceOfSnow = 5,
                condition = condition,
                uv = 1.0,
                airQuality = airQuality
            ),
            hour = listOf(
                Hour(
                    condition = condition,
                    tempF = 20.0,
                    tempC = 20.0,
                    chanceOfRain = 0,
                    timeEpoch = System.currentTimeMillis(),
                    time = "",
                    isDay = 1,
                    windMph = 1.0,
                    windKph = 1.0,
                    windDegree = 1,
                    windDir = "SW",
                    pressureMb = 1.0,
                    pressureIn = 1.0,
                    precipMm = 1.0,
                    precipIn = 1.0,
                    humidity = 24,
                    cloud = 24,
                    feelslikeC = 1.0,
                    feelslikeF = 1.0,
                    windchillC = 1.0,
                    windchillF = 1.0,
                    heatindexC = 1.0,
                    heatindexF = 1.0,
                    dewpointC = 1.0,
                    dewpointF = 1.0,
                    willItRain = 1,
                    willItSnow = 1,
                    chanceOfSnow = 20,
                    visKm = 1.0,
                    visMiles = 1.0,
                    gustKph = 1.0,
                    gustMph = 1.0,
                    uv = 1.0,
                    airQuality = airQuality
                ),
            )
        )

        val LOADING_DATA = ForecastResponse(
            location = Location(
                name = "Seattle",
                region = "",
                country = "",
                lat = 0.0,
                lon = 0.0,
                tzId = "",
                localtimeEpoch = 0,
                localtime = ""
            ),
            current = Current(
                lastUpdatedEpoch = System.currentTimeMillis(),
                tempF = 40.0,
                tempC = 40.0,
                windMph = 2.0,
                windKph = 2.0,
                windDir = "SW",
                humidity = 29,
                cloud = 20,
                feelslikeF = 20.0,
                feelslikeC = 20.0,
                visMiles = 6.0,
                visKm = 6.0,
                airQuality = airQuality,
                lastUpdated = "",
                isDay = 1,
                condition = condition,
                windDegree = 1,
                pressureMb = 0.0,
                pressureIn = 0.0,
                precipMm = 0.0,
                precipIn = 0.0,
                uv = 1.0,
                gustMph = 0.0,
                gustKph = 0.0,
            ),
            forecast = Forecast(
                forecastday = listOf(
                    forecastDay,
                    forecastDay,
                    forecastDay,
                    forecastDay,
                    forecastDay
                )
            ),
            alerts = Alerts(
                alert = listOf(
                    WeatherAlert(
                        event = "Test event",
                        severity = "Unknown"
                    )
                )
            )
        )
    }
}