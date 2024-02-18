package dev.joshhalvorson.materialweather.data.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val LAST_GENERATED_ALERT = stringPreferencesKey("last_generated_alert")
private val GENERATED_ALERT_TEXT = stringPreferencesKey("generated_alert_text")
private val USE_DARK_MODE = stringPreferencesKey("use_dark_mode")
private val TEMPERATURE_UNITS = stringPreferencesKey("temp_units")
private val PHYSICAL_UNITS = stringPreferencesKey("physical_units")

private val TRIGGER_REFRESH = booleanPreferencesKey("trigger_refresh")

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun Context.lastGeneratedAlertFlow(): Flow<String> = dataStore.data.map { preferences ->
    preferences[LAST_GENERATED_ALERT] ?: ""
}

fun Context.generatedAlertTextFlow(): Flow<String> = dataStore.data.map { preferences ->
    preferences[GENERATED_ALERT_TEXT] ?: ""
}

fun Context.useDarkModeFlow(): Flow<String?> = dataStore.data.map { preferences ->
    preferences[USE_DARK_MODE]
}

fun Context.temperatureUnitsFlow(): Flow<String?> = dataStore.data.map { preferences ->
    preferences[TEMPERATURE_UNITS]
}

fun Context.physicalUnitsFlow(): Flow<String?> = dataStore.data.map { preferences ->
    preferences[PHYSICAL_UNITS]
}

fun Context.triggerRefreshFlow(): Flow<Boolean?> = dataStore.data.map { preferences ->
    preferences[TRIGGER_REFRESH] ?: false
}

suspend fun Context.storeLastGeneratedAlert(lastGeneratedAlert: String) {
    dataStore.edit { preferences ->
        preferences[LAST_GENERATED_ALERT] = lastGeneratedAlert
    }
}

suspend fun Context.storeGeneratedAlertText(generatedAlertText: String) {
    dataStore.edit { preferences ->
        preferences[GENERATED_ALERT_TEXT] = generatedAlertText
    }
}

suspend fun Context.storeUseDarkMode(userDarkMode: String) {
    dataStore.edit { preferences ->
        preferences[USE_DARK_MODE] = userDarkMode
    }
}

suspend fun Context.storeTemperatureUnits(temperatureUnits: String) {
    dataStore.edit { preferences ->
        preferences[TEMPERATURE_UNITS] = temperatureUnits
    }
}

suspend fun Context.storePhysicalUnits(physicalUnits: String) {
    dataStore.edit { preferences ->
        preferences[PHYSICAL_UNITS] = physicalUnits
    }
}

suspend fun Context.storeTriggerRefresh(triggerRefresh: Boolean) {
    dataStore.edit { preferences ->
        preferences[TRIGGER_REFRESH] = triggerRefresh
    }
}