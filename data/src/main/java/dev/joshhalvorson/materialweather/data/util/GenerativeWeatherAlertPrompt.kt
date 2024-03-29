package dev.joshhalvorson.materialweather.data.util

import dev.joshhalvorson.materialweather.data.models.weather.Day

fun getGenerativeWeatherAlertPrompt(tomorrowsWeather: Day, tempUnit: String, unit: String): String {
    return """
        Can you give me an interesting piece of information in tomorrows weather ($tomorrowsWeather) 
        in $tempUnit and $unit units. Only using the weather during the day, ignoring the high and low temperatures,
        rain, and snow, phrased as a sentence. Also please round any number with a decimal to the
         nearest whole number. Don't include bullet points
        
        Can you also give some advice on being ready for tomorrows weather, phrased in a helpful way.
         You can include bullet points for this
    """.trimIndent()
}