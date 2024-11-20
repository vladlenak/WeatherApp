package t.me.octopusapps.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import t.me.octopusapps.weatherapp.data.model.CityResponse
import t.me.octopusapps.weatherapp.data.model.FiveDayThreeHourForecastResponse

interface WeatherApiService {

    @GET("geo/1.0/direct")
    suspend fun getCityCoordinates(
        @Query("q") city: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<CityResponse>

    @GET("data/2.5/forecast")
    suspend fun getFiveDayThreeHourForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String
    ): FiveDayThreeHourForecastResponse
}
