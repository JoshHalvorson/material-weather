package dev.joshhalvorson.materialweather.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.ui.components.MaterialWeatherTopAppBar
import dev.joshhalvorson.materialweather.ui.viewmodel.SettingsViewModel
import dev.joshhalvorson.materialweather.util.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigateTo: (NavigationRoute) -> Unit
) {
    // TODO location
    // TODO colors
    // TODO types of alerts
    // TODO data displayed in main card

    val themeSelectedIndex by viewModel.themeSelectedIndex.collectAsStateWithLifecycle()
    val tempSelectedIndex by viewModel.tempSelectedIndex.collectAsStateWithLifecycle()
    val unitsSelectedIndex by viewModel.unitsSelectedIndex.collectAsStateWithLifecycle()

    /**
     * Main content
     */
    Column {
        MaterialWeatherTopAppBar(
            title = stringResource(R.string.settings_title),
            navigationIcon = {
                IconButton(onClick = { navigateTo(NavigationRoute.Back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /**
             * Units settings
             */
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.units),
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.temperature))
                    SingleChoiceSegmentedButtonRow {
                        viewModel.temperatureOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = viewModel.temperatureOptions.size
                                ),
                                onClick = { viewModel.onTemperatureClicked(index = index) },
                                selected = index == tempSelectedIndex
                            ) {
                                Text(label)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.units))
                    SingleChoiceSegmentedButtonRow {
                        viewModel.unitOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = viewModel.unitOptions.size
                                ),
                                onClick = { viewModel.onUnitsClicked(index = index) },
                                selected = index == unitsSelectedIndex
                            ) {
                                Text(label)
                            }
                        }
                    }
                }
            }

            /**
             * Appearance settings
             */
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.app_theme))
                    SingleChoiceSegmentedButtonRow {
                        viewModel.themeOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = viewModel.themeOptions.size
                                ),
                                onClick = { viewModel.onThemeClicked(index = index) },
                                selected = index == themeSelectedIndex
                            ) {
                                Text(label)
                            }
                        }
                    }
                }
            }
        }
    }
}