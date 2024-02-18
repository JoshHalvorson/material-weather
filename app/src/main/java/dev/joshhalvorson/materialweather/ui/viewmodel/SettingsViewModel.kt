package dev.joshhalvorson.materialweather.ui.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.util.Key
import dev.joshhalvorson.materialweather.data.util.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    val themeOptions = listOf(
        application.getString(R.string.system),
        application.getString(R.string.light),
        application.getString(R.string.dark)
    )
    val temperatureOptions = listOf(
        application.getString(R.string.fahrenheit),
        application.getString(R.string.celsius)
    )
    val unitOptions = listOf(
        application.getString(R.string.imperial),
        application.getString(R.string.metric)
    )

    private val mThemeSelectedIndex = MutableStateFlow(0)
    val themeSelectedIndex = mThemeSelectedIndex.asStateFlow()

    private val mTempSelectedIndex = MutableStateFlow(0)
    val tempSelectedIndex = mTempSelectedIndex.asStateFlow()

    private val mUnitsSelectedIndex = MutableStateFlow(0)
    val unitsSelectedIndex = mUnitsSelectedIndex.asStateFlow()

    init {
        getTempSetting()
        getThemeSetting()
        getUnitsSetting()
    }

    private fun getTempSetting() = viewModelScope.launch {
        application.applicationContext.dataStore.data.collect { preferences ->
            mTempSelectedIndex.emit(
                temperatureOptions.indexOf(
                    preferences[Key.TEMPERATURE_UNITS] ?: temperatureOptions.first()
                )
            )
        }
    }

    private fun getThemeSetting() = viewModelScope.launch {
        application.applicationContext.dataStore.data.collect { preferences ->
            mThemeSelectedIndex.emit(
                themeOptions.indexOf(
                    preferences[Key.USE_DARK_MODE] ?: themeOptions.first()
                )
            )
        }
    }

    private fun getUnitsSetting() = viewModelScope.launch {
        application.applicationContext.dataStore.data.collect { preferences ->
            mUnitsSelectedIndex.emit(
                unitOptions.indexOf(
                    preferences[Key.PHYSICAL_UNITS] ?: unitOptions.first()
                )
            )
        }
    }

    fun onUnitsClicked(index: Int) = viewModelScope.launch {
        mUnitsSelectedIndex.emit(index)
        application.applicationContext.dataStore.edit { settings ->
            settings[Key.PHYSICAL_UNITS] = unitOptions[index]
        }
    }

    fun onTemperatureClicked(index: Int) = viewModelScope.launch {
        mTempSelectedIndex.emit(index)
        application.applicationContext.dataStore.edit { settings ->
            settings[Key.TEMPERATURE_UNITS] = temperatureOptions[index]
        }
    }

    fun onThemeClicked(index: Int) = viewModelScope.launch {
        mThemeSelectedIndex.emit(index)
        application.applicationContext.dataStore.edit { settings ->
            settings[Key.USE_DARK_MODE] = themeOptions[index]
        }

        val uiModeManger = application.getSystemService(UI_MODE_SERVICE) as UiModeManager
        uiModeManger.setApplicationNightMode(
            when (themeOptions[index]) {
                application.getString(R.string.system) -> {
                    UiModeManager.MODE_NIGHT_AUTO
                }

                application.getString(R.string.light) -> {
                    UiModeManager.MODE_NIGHT_NO
                }

                application.getString(R.string.dark) -> {
                    UiModeManager.MODE_NIGHT_YES
                }

                else -> {
                    UiModeManager.MODE_NIGHT_AUTO
                }
            }
        )
    }
}