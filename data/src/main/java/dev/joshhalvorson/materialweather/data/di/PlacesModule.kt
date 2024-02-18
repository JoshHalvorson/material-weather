package dev.joshhalvorson.materialweather.data.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {
    @Provides
    @Singleton
    fun providePlacesClient(
        @ApplicationContext
        applicationContext: Context
    ): PlacesClient = Places.createClient(applicationContext)

    @Provides
    @Singleton
    fun provideSessionToken(): AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
}