package dev.joshhalvorson.materialweather

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import dev.joshhalvorson.materialweather.data.BuildConfig

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.PLACES_KEY)
    }
}