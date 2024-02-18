package dev.joshhalvorson.materialweather.data.models.location

import com.google.android.gms.maps.model.LatLng

data class SavedLocation(
    val display: String,
    val latLng: LatLng
)