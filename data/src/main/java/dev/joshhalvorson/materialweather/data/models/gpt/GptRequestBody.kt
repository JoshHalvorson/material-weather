package dev.joshhalvorson.materialweather.data.models.gpt


data class GptRequestBody(
    val model: String = "text-davinci-003",
    val temperature: Double = 0.3,
    val prompt: String,
    val max_tokens: Int = 400
)