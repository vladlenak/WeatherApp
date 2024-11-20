package t.me.octopusapps.weatherapp.ui.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import t.me.octopusapps.weatherapp.BuildConfig
import t.me.octopusapps.weatherapp.data.remote.RetrofitInstance

class LocationViewModel : ViewModel() {

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState

    fun checkLocationPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCurrentLocation(context)
        } else {
            _locationState.value = LocationState.PermissionRequired
        }
    }

    private fun fetchCurrentLocation(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _locationState.value = LocationState.LocationFetched(location.latitude, location.longitude)
                } else {
                    _locationState.value = LocationState.Error("Unable to fetch location")
                }
            }.addOnFailureListener {
                _locationState.value = LocationState.Error("Failed to get location")
            }
        } else {
            _locationState.value = LocationState.Error("Location permission not granted")
        }
    }


    fun fetchCityCoordinates(cityName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCityCoordinates(
                    city = cityName,
                    apiKey = BuildConfig.API_KEY
                )

                if (response.isNotEmpty()) {
                    val cityData = response.first()
                    _locationState.value = LocationState.LocationFetched(cityData.lat, cityData.lon)
                } else {
                    _locationState.value = LocationState.Error("City not found")
                }
            } catch (e: HttpException) {
                _locationState.value = LocationState.Error("Error fetching city data")
            } catch (e: Exception) {
                _locationState.value = LocationState.Error("An unexpected error occurred")
            }
        }
    }
}

sealed class LocationState {
    data object Idle : LocationState()
    data object PermissionRequired : LocationState()
    data class LocationFetched(val latitude: Double, val longitude: Double) : LocationState()
    data class Error(val message: String) : LocationState()
}
