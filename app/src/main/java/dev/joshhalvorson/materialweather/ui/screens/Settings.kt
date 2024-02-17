package dev.joshhalvorson.materialweather.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.ui.components.MaterialWeatherTopAppBar
import dev.joshhalvorson.materialweather.util.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigateTo: (NavigationRoute) -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

        }
    }
}