package dev.joshhalvorson.materialweather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.ui.components.MaterialWeatherTopAppBar
import dev.joshhalvorson.materialweather.ui.viewmodel.LocationSearchViewModel
import dev.joshhalvorson.materialweather.util.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearch(
    viewModel: LocationSearchViewModel = hiltViewModel(),
    navigateTo: (NavigationRoute) -> Unit,
) {
    val locationSearchText by viewModel.locationSearchText.collectAsStateWithLifecycle()
    val locationSearchResultsLoading by viewModel.locationSearchResultsLoading.collectAsStateWithLifecycle()
    val locationSearchResults by viewModel.locationSearchResults.collectAsStateWithLifecycle()

    Column {
        MaterialWeatherTopAppBar(title = "Add location", navigationIcon = {
            IconButton(onClick = { navigateTo(NavigationRoute.Back) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            TextField(modifier = Modifier.fillMaxWidth(),
                value = locationSearchText,
                onValueChange = viewModel::onLocationSearchTextChanged,
                singleLine = true,
                label = {
                    Text(
                        text = "Search for a place", style = MaterialTheme.typography.labelMedium
                    )
                })
        }

        LazyColumn {
            items(items = locationSearchResults, key = { it.placeId }) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.saveLocation(
                            location = it.placeId,
                            onFinished = { navigateTo(NavigationRoute.Back) })
                    }) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(text = it.getFullText(null).toString())
                    }
                }
            }
        }
    }
}