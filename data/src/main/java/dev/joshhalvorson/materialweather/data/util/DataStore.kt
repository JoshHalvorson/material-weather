package dev.joshhalvorson.materialweather.data.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.joshhalvorson.materialweather.data.models.location.SavedLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val LAST_GENERATED_ALERT = stringPreferencesKey("last_generated_alert")
private val GENERATED_ALERT_TEXT = stringPreferencesKey("generated_alert_text")
private val USE_DARK_MODE = stringPreferencesKey("use_dark_mode")
private val TEMPERATURE_UNITS = stringPreferencesKey("temp_units")
private val PHYSICAL_UNITS = stringPreferencesKey("physical_units")
private val LOCATION_OPTION = stringPreferencesKey("location_option")
private val SAVED_LOCATIONS = stringPreferencesKey("saved_locations")
private val ACTIVE_LOCATION = stringPreferencesKey("active_location")

private val TRIGGER_REFRESH = booleanPreferencesKey("trigger_refresh")
private val HAS_CHANGED_UNIT = booleanPreferencesKey("has_changed_unit")

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun Context.activeLocationFlow(): Flow<SavedLocation?> = dataStore.data.map { preferences ->
    Gson().fromJson(preferences[ACTIVE_LOCATION], SavedLocation::class.java)
}

fun Context.savedLocationsFlow(): Flow<List<SavedLocation>?> = dataStore.data.map { preferences ->
    val listType = object : TypeToken<List<SavedLocation>?>() {}.type
    Gson().fromJson(preferences[SAVED_LOCATIONS], listType)
}

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

fun Context.hasChangedUnitFlow(): Flow<Boolean?> = dataStore.data.map { preferences ->
    preferences[HAS_CHANGED_UNIT] ?: false
}

fun Context.locationOptionFlow(): Flow<String?> = dataStore.data.map { preferences ->
    preferences[LOCATION_OPTION]
}

suspend fun Context.storeActiveLocation(activeLocation: SavedLocation?) {
    dataStore.edit { preferences ->
        preferences[ACTIVE_LOCATION] = activeLocation?.let { Gson().toJson(activeLocation) } ?: ""
    }
}

suspend fun Context.storeLocations(location: SavedLocation) {
    val savedLocations = savedLocationsFlow().first()
    val newList = mutableListOf<SavedLocation>()
    newList.addAll(savedLocations ?: emptyList())
    newList.add(location)

    dataStore.edit { preferences ->
        preferences[SAVED_LOCATIONS] = Gson().toJson(newList.toList())
    }
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

suspend fun Context.storeHasChangedUnit(hasChangedUnit: Boolean) {
    dataStore.edit { preferences ->
        preferences[HAS_CHANGED_UNIT] = hasChangedUnit
    }
}

suspend fun Context.storeLocationOption(locationOption: String) {
    dataStore.edit { preferences ->
        preferences[LOCATION_OPTION] = locationOption
    }
}