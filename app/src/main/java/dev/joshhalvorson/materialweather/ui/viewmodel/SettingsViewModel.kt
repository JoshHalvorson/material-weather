package dev.joshhalvorson.materialweather.ui.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.util.physicalUnitsFlow
import dev.joshhalvorson.materialweather.data.util.storeHasChangedUnit
import dev.joshhalvorson.materialweather.data.util.storePhysicalUnits
import dev.joshhalvorson.materialweather.data.util.storeTemperatureUnits
import dev.joshhalvorson.materialweather.data.util.storeTriggerRefresh
import dev.joshhalvorson.materialweather.data.util.storeUseDarkMode
import dev.joshhalvorson.materialweather.data.util.temperatureUnitsFlow
import dev.joshhalvorson.materialweather.data.util.useDarkModeFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        mTempSelectedIndex.emit(
            temperatureOptions.indexOf(
                application.applicationContext.temperatureUnitsFlow().first()
                    ?: temperatureOptions.first()
            )
        )
    }

    private fun getThemeSetting() = viewModelScope.launch {
        mThemeSelectedIndex.emit(
            themeOptions.indexOf(
                application.applicationContext.useDarkModeFlow().first() ?: themeOptions.first()
            )
        )
    }

    private fun getUnitsSetting() = viewModelScope.launch {
        mUnitsSelectedIndex.emit(
            unitOptions.indexOf(
                application.applicationContext.physicalUnitsFlow().first() ?: unitOptions.first()
            )
        )
    }

    private fun updateHomeState() = viewModelScope.launch {
        application.applicationContext.storeTriggerRefresh(triggerRefresh = true)
        application.applicationContext.storeHasChangedUnit(hasChangedUnit = true)
    }

    fun onUnitsClicked(index: Int) = viewModelScope.launch {
        mUnitsSelectedIndex.emit(index)
        application.applicationContext.storePhysicalUnits(physicalUnits = unitOptions[index])
        updateHomeState()
    }

    fun onTemperatureClicked(index: Int) = viewModelScope.launch {
        mTempSelectedIndex.emit(index)
        application.applicationContext.storeTemperatureUnits(temperatureUnits = temperatureOptions[index])
        updateHomeState()
    }

    fun onThemeClicked(index: Int) = viewModelScope.launch {
        mThemeSelectedIndex.emit(index)
        application.applicationContext.storeUseDarkMode(userDarkMode = themeOptions[index])

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