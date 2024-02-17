package dev.joshhalvorson.materialweather.ui.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
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
    val options = listOf(
        application.getString(R.string.system),
        application.getString(R.string.light),
        application.getString(R.string.dark)
    )

    private val mThemeSelectedIndex = MutableStateFlow(0)
    val themeSelectedIndex = mThemeSelectedIndex.asStateFlow()

    init {
        viewModelScope.launch {
            application.applicationContext.dataStore.data.collectLatest { preferences ->
                mThemeSelectedIndex.emit(
                    options.indexOf(
                        preferences[Key.USE_DARK_MODE] ?: options.first()
                    )
                )
            }
        }
    }

    fun onThemeClicked(index: Int) = viewModelScope.launch {
        mThemeSelectedIndex.emit(index)
        application.applicationContext.dataStore.edit { settings ->
            settings[Key.USE_DARK_MODE] = options[index]
        }

        val uiModeManger = application.getSystemService(UI_MODE_SERVICE) as UiModeManager
        uiModeManger.setApplicationNightMode(
            when (options[index]) {
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