package dev.joshhalvorson.materialweather.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.ui.components.MaterialWeatherTopAppBar
import dev.joshhalvorson.materialweather.ui.viewmodel.SettingsViewModel
import dev.joshhalvorson.materialweather.util.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(), navigateTo: (NavigationRoute) -> Unit
) {
    // TODO location
    // TODO colors
    // TODO types of alerts
    // TODO data displayed in main card

    val themeSelectedIndex by viewModel.themeSelectedIndex.collectAsStateWithLifecycle()
    val tempSelectedIndex by viewModel.tempSelectedIndex.collectAsStateWithLifecycle()
    val unitsSelectedIndex by viewModel.unitsSelectedIndex.collectAsStateWithLifecycle()
    val locationSelectedIndex by viewModel.locationSelectedIndex.collectAsStateWithLifecycle()
    val savedLocations by viewModel.savedLocations.collectAsStateWithLifecycle()
    val savedLocationsVisible by viewModel.savedLocationsVisible.collectAsStateWithLifecycle()
    val activeLocation by viewModel.activeLocation.collectAsStateWithLifecycle()
    val navigateToLocationSearch by viewModel.navigateToLocationSearch.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToLocationSearch) {
        if (navigateToLocationSearch) {
            navigateTo(NavigationRoute.LocationSearch)
            viewModel.resetNavigateToLocationSearch()
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        if (activeLocation == null) {
            viewModel.onLocationClicked(index = 0, refresh = false)
        }
    }

    /**
     * Main content
     */
    Column {
        MaterialWeatherTopAppBar(title = stringResource(R.string.settings_title), navigationIcon = {
            IconButton(onClick = { navigateTo(NavigationRoute.Back) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /**
             * Location settings
             */
            SettingsSection {
                SectionLabel(text = stringResource(R.string.location))

                Column {
                    OptionRow {
                        OptionLabel(text = stringResource(R.string.forecast_location))
                        MaterialSegmentedButton {
                            viewModel.locationOptions.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index, count = viewModel.locationOptions.size
                                    ), onClick = {
                                        viewModel.onLocationClicked(
                                            index = index, refresh = true
                                        )
                                    }, selected = index == locationSelectedIndex
                                ) {
                                    ButtonText(text = label)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        modifier = Modifier.padding(8.dp), visible = savedLocationsVisible
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            savedLocations.forEach {
                                Text(modifier = Modifier.clickable {
                                    viewModel.onSavedLocationClicked(it)
                                }, text = "${it.display} - ACTIVE = ${it == activeLocation}")
                            }
                            TextButton(modifier = Modifier.align(Alignment.End),
                                onClick = { navigateTo(NavigationRoute.LocationSearch) }) {
                                Text(text = "Add location")
                            }
                        }
                    }
                }

            }

            /**
             * Units settings
             */
            SettingsSection {
                SectionLabel(text = stringResource(R.string.units))

                OptionRow {
                    OptionLabel(text = stringResource(R.string.temperature))
                    MaterialSegmentedButton {
                        viewModel.temperatureOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = viewModel.temperatureOptions.size
                                ),
                                onClick = { viewModel.onTemperatureClicked(index = index) },
                                selected = index == tempSelectedIndex
                            ) {
                                ButtonText(text = label)
                            }
                        }
                    }
                }

                OptionRow {
                    OptionLabel(text = stringResource(id = R.string.units))
                    MaterialSegmentedButton {
                        viewModel.unitOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = viewModel.unitOptions.size
                                ),
                                onClick = { viewModel.onUnitsClicked(index = index) },
                                selected = index == unitsSelectedIndex
                            ) {
                                ButtonText(text = label)
                            }
                        }
                    }
                }
            }

            /**
             * Appearance settings
             */
            SettingsSection {
                SectionLabel(text = stringResource(R.string.appearance))

                OptionRow {
                    OptionLabel(text = stringResource(R.string.app_theme))
                    MaterialSegmentedButton {
                        viewModel.themeOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = viewModel.themeOptions.size
                                ),
                                onClick = { viewModel.onThemeClicked(index = index) },
                                selected = index == themeSelectedIndex
                            ) {
                                ButtonText(text = label)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun SettingsSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelMedium)
}

@Composable
private fun OptionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelLarge)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.MaterialSegmentedButton(content: @Composable SingleChoiceSegmentedButtonRowScope.() -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier, content = content)
}

@Composable
private fun ButtonText(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelMedium)
}