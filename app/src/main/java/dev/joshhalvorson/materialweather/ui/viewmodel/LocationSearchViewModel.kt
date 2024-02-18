package dev.joshhalvorson.materialweather.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.location.SavedLocation
import dev.joshhalvorson.materialweather.data.repository.location.LocationSearchRepository
import dev.joshhalvorson.materialweather.data.util.storeActiveLocation
import dev.joshhalvorson.materialweather.data.util.storeHasChangedUnit
import dev.joshhalvorson.materialweather.data.util.storeLocationOption
import dev.joshhalvorson.materialweather.data.util.storeLocations
import dev.joshhalvorson.materialweather.data.util.storeTriggerRefresh
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val application: Application,
    private val locationSearchRepository: LocationSearchRepository,
) : ViewModel() {
    private val mSavedLocations = MutableStateFlow(emptySet<String>())
    val savedLocations = mSavedLocations.asStateFlow()

    private val mLocationSearchResultsLoading = MutableStateFlow(false)
    val locationSearchResultsLoading = mLocationSearchResultsLoading.asStateFlow()

    private val mLocationSearchText = MutableStateFlow("")
    val locationSearchText = mLocationSearchText.asStateFlow()

    private val mLocationSearchResults = MutableStateFlow(emptyList<AutocompletePrediction>())
    val locationSearchResults = mLocationSearchResults.asStateFlow()

    fun onLocationSearchTextChanged(search: String) = viewModelScope.launch {
        mLocationSearchText.emit(search)

        if (mLocationSearchText.value.length >= 3) {
            locationSearchRepository.locationSearch(search = mLocationSearchText.value)
                .onStart { mLocationSearchResultsLoading.emit(true) }
                .catch { mLocationSearchResultsLoading.emit(false) }.collect {
                    mLocationSearchResults.emit(it ?: emptyList())
                    mLocationSearchResultsLoading.emit(false)
                }
        } else {
            mLocationSearchResults.emit(emptyList())
        }
    }

    fun saveLocation(location: String, onFinished: () -> Unit) = viewModelScope.launch {
        locationSearchRepository.getLocationLatitudeAndLongitude(placeId = location)
            .onStart { mLocationSearchResultsLoading.emit(true) }
            .catch { mLocationSearchResultsLoading.emit(false) }.collect { place ->
                place?.takeIf { it.name != null && it.latLng != null }?.let {
                    with(application.applicationContext) {
                        val savedLocation = SavedLocation(
                            display = it.name!!,
                            latLng = it.latLng!!
                        )
                        storeLocations(location = savedLocation)
                        storeActiveLocation(activeLocation = savedLocation)
                        storeLocationOption(locationOption = getString(R.string.set))
                        storeTriggerRefresh(triggerRefresh = true)
                        storeHasChangedUnit(hasChangedUnit = true)
                        onFinished()
                    }
                } ?: run {
                    // ERROR
                }
            }

    }
}