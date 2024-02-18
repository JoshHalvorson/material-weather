package dev.joshhalvorson.materialweather.data.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Key {
    val LAST_GENERATED_ALERT = stringPreferencesKey("last_generated_alert")
    val GENERATED_ALERT_TEXT = stringPreferencesKey("generated_alert_text")
    val USE_DARK_MODE = stringPreferencesKey("use_dark_mode")
    val TEMPERATURE_UNITS = stringPreferencesKey("temp_units")
    val PHYSICAL_UNITS = stringPreferencesKey("physical_units")
}