package dev.joshhalvorson.materialweather.ui.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.location.SavedLocation
import dev.joshhalvorson.materialweather.data.util.activeLocationFlow
import dev.joshhalvorson.materialweather.data.util.locationOptionFlow
import dev.joshhalvorson.materialweather.data.util.physicalUnitsFlow
import dev.joshhalvorson.materialweather.data.util.savedLocationsFlow
import dev.joshhalvorson.materialweather.data.util.storeActiveLocation
import dev.joshhalvorson.materialweather.data.util.storeHasChangedUnit
import dev.joshhalvorson.materialweather.data.util.storeLocationOption
import dev.joshhalvorson.materialweather.data.util.storePhysicalUnits
import dev.joshhalvorson.materialweather.data.util.storeTemperatureUnits
import dev.joshhalvorson.materialweather.data.util.storeTriggerRefresh
import dev.joshhalvorson.materialweather.data.util.storeUseDarkMode
import dev.joshhalvorson.materialweather.data.util.temperatureUnitsFlow
import dev.joshhalvorson.materialweather.data.util.useDarkModeFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
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
    val locationOptions = listOf(
        application.getString(R.string.device),
        application.getString(R.string.set)
    )

    private val mThemeSelectedIndex = MutableStateFlow(0)
    val themeSelectedIndex = mThemeSelectedIndex.asStateFlow()

    private val mTempSelectedIndex = MutableStateFlow(0)
    val tempSelectedIndex = mTempSelectedIndex.asStateFlow()

    private val mUnitsSelectedIndex = MutableStateFlow(0)
    val unitsSelectedIndex = mUnitsSelectedIndex.asStateFlow()

    private val mLocationSelectedIndex = MutableStateFlow(0)
    val locationSelectedIndex = mLocationSelectedIndex.asStateFlow()

    private val mNavigateToLocationSearch = MutableStateFlow(false)
    val navigateToLocationSearch = mNavigateToLocationSearch.asStateFlow()

    private val mSavedLocationsVisible = MutableStateFlow(false)
    val savedLocationsVisible = mSavedLocationsVisible.asStateFlow()

    private val mSavedLocations = MutableStateFlow(emptyList<SavedLocation>())
    val savedLocations = mSavedLocations.asStateFlow()

    private val mActiveLocation = MutableStateFlow<SavedLocation?>(null)
    val activeLocation = mActiveLocation.asStateFlow()

    init {
        getTempSetting()
        getThemeSetting()
        getUnitsSetting()
        getLocationSetting()
        getSavedLocations()
        getActiveLocation()
    }

    fun resetNavigateToLocationSearch() = viewModelScope.launch {
        mNavigateToLocationSearch.emit(false)
    }

    fun onSavedLocationClicked(savedLocation: SavedLocation) = viewModelScope.launch {
        mActiveLocation.emit(savedLocation)
        with(application.applicationContext) {
            storeActiveLocation(savedLocation)
            storeLocationOption(getString(R.string.set))
            updateHomeState()
        }
    }

    fun onLocationClicked(index: Int, refresh: Boolean) = viewModelScope.launch {
        mLocationSelectedIndex.emit(index)
        when (val newSetting = locationOptions[mLocationSelectedIndex.value]) {
            application.applicationContext.getString(R.string.set) -> {
                if (mSavedLocations.value.isEmpty()) {
                    mNavigateToLocationSearch.emit(true)
                } else {
                    mSavedLocationsVisible.emit(true)
                }
            }

            application.applicationContext.getString(R.string.device) -> {
                if (refresh && application.applicationContext.activeLocationFlow()
                        .first() != null
                ) {
                    updateHomeState()
                }

                application.applicationContext.storeLocationOption(locationOption = newSetting)
                application.applicationContext.storeActiveLocation(activeLocation = null)
                mSavedLocationsVisible.emit(false)
            }
        }
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

    private fun getSavedLocations() = viewModelScope.launch {
        application.applicationContext.savedLocationsFlow().collectLatest {
            mSavedLocations.emit(it ?: emptyList())
            if (mSavedLocations.value.isNotEmpty() && mLocationSelectedIndex.value == locationOptions.indexOf(
                    application.applicationContext.getString(R.string.set)
                )
            ) {
                mSavedLocationsVisible.emit(true)
            }
        }
    }

    private fun getActiveLocation() = viewModelScope.launch {
        application.applicationContext.activeLocationFlow().collectLatest {
            mActiveLocation.emit(it)
        }
    }

    private fun getLocationSetting() = viewModelScope.launch {
        mLocationSelectedIndex.emit(
            locationOptions.indexOf(
                application.applicationContext.locationOptionFlow().first()
                    ?: locationOptions.first()
            )
        )
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
}