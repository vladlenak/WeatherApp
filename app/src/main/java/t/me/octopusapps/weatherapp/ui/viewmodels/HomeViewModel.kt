package t.me.octopusapps.weatherapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import t.me.octopusapps.weatherapp.BuildConfig
import t.me.octopusapps.weatherapp.data.model.FiveDayThreeHourForecastResponse
import t.me.octopusapps.weatherapp.data.remote.RetrofitInstance

class HomeViewModel : ViewModel() {

    private val _weatherState = MutableStateFlow<HomeState>(HomeState.Idle)
    val weatherState: StateFlow<HomeState> = _weatherState

    fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getFiveDayThreeHourForecast(
                    lat = lat,
                    lon = lon,
                    units = "metric",
                    lang = "en",
                    apiKey = BuildConfig.API_KEY
                )
                _weatherState.value = HomeState.DataLoaded(response)
            } catch (e: HttpException) {
                _weatherState.value = HomeState.Error("Failed to fetch weather data")
            } catch (e: Exception) {
                _weatherState.value = HomeState.Error("An unexpected error occurred")
            }
        }
    }
}

sealed class HomeState {
    data object Idle : HomeState()
    data class DataLoaded(val weatherData: FiveDayThreeHourForecastResponse) : HomeState()
    data class Error(val message: String) : HomeState()
}
