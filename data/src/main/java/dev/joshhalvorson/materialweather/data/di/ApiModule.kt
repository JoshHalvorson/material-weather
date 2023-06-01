package dev.joshhalvorson.materialweather.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.joshhalvorson.materialweather.data.remote.gpt.GptApi
import dev.joshhalvorson.materialweather.data.remote.weather.WeatherApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun provideWeatherApi(
        @WeatherRetrofit
        retrofit: Retrofit
    ): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    fun provideGptApi(
        @GptRetrofit
        retrofit: Retrofit
    ): GptApi {
        return retrofit.create(GptApi::class.java)
    }
}