package dev.joshhalvorson.materialweather.data.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Key {
    val LAST_GPT_ALERT = stringPreferencesKey("last_gpt_alert")
    val GPT_ALERT_TEXT = stringPreferencesKey("gpt_alert_text")
}