package dev.joshhalvorson.materialweather.data.repository.location

import android.content.Context
import android.util.Log
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocationSearchRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val placesClient: PlacesClient,
    private val token: AutocompleteSessionToken,
) {
    suspend fun locationSearch(search: String): Flow<List<AutocompletePrediction>?> = callbackFlow {
        try {
            val request =
                FindAutocompletePredictionsRequest.builder().setQuery(search).setSessionToken(token)
                    .setTypesFilter(listOf(PlaceTypes.GEOCODE)).build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                Log.i(
                    "LocationSearchRepository", "results: ${response.autocompletePredictions}"
                )
                trySend(response.autocompletePredictions.toList())
            }.addOnFailureListener {
                Log.e("LocationSearchRepository", "Error in findAutocompletePredictions", it)
                trySend(null)
            }
        } catch (e: Exception) {
            Log.e("LocationSearchRepository", "Error in locationSearch", e)
            trySend(null)
        }

        awaitClose { }
    }.flowOn(Dispatchers.IO)

    suspend fun getLocationLatitudeAndLongitude(placeId: String): Flow<Place?> = callbackFlow {
        try {
            val request =
                FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME))
                    .setSessionToken(token).build()

            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                Log.i(
                    "LocationSearchRepository", "results: ${response.place}"
                )
                trySend(response.place)
            }.addOnFailureListener {
                Log.e("LocationSearchRepository", "Error in fetchPlace", it)
                trySend(null)
            }

        } catch (e: Exception) {
            Log.e("LocationSearchRepository", "Error in getLocationLatitudeAndLongitude", e)
            trySend(null)
        }

        awaitClose { }
    }.flowOn(Dispatchers.IO)
}