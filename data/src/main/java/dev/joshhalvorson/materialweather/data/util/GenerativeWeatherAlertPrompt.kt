package dev.joshhalvorson.materialweather.data.util

import dev.joshhalvorson.materialweather.data.models.weather.Day

fun getGenerativeWeatherAlertPrompt(tomorrowsWeather: Day, unit: String): String {
    return """
        Can you give me an interesting piece of information in tomorrows weather ($tomorrowsWeather) 
        in $unit units, ignoring the high and low temperatures, rain, and snow, phrased as a sentence. 
        Also please round any number with a decimal to the nearest whole number. 
        
        Can you also give some advice on being ready for tomorrows weather, phrased in a helpful way.
    """.trimIndent()
}