package t.me.octopusapps.weatherapp.ui.fragments

import androidx.navigation.fragment.findNavController
import t.me.octopusapps.weatherapp.data.model.FiveDayThreeHourForecastResponse
import t.me.octopusapps.weatherapp.data.model.ForecastItem
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import t.me.octopusapps.weatherapp.databinding.FragmentHomeBinding
import t.me.octopusapps.weatherapp.ui.adapters.DailyForecastAdapter
import t.me.octopusapps.weatherapp.ui.adapters.HourlyForecastAdapter
import t.me.octopusapps.weatherapp.ui.viewmodels.HomeState
import t.me.octopusapps.weatherapp.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = HomeFragmentArgs.fromBundle(requireArguments())
        val latitude = args.latitude.toDouble()
        val longitude = args.longitude.toDouble()

        observeViewModel()
        setupButtonListeners()

        viewModel.fetchWeatherData(latitude, longitude)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToLocationFragment()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.weatherState.collectLatest { state ->
                when (state) {
                    is HomeState.Idle -> {}

                    is HomeState.DataLoaded -> {
                        updateUI(state.weatherData)
                    }

                    is HomeState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupButtonListeners() {
        binding.btnNextForecast.setOnClickListener {
            showNextForecast()
        }

        binding.btnHome.setOnClickListener {
            showHourlyForecast()
        }
    }

    private fun showNextForecast() {
        binding.hourlyForecastContainer.visibility = View.GONE
        binding.nextForecastContainer.visibility = View.VISIBLE
        binding.btnNextForecast.visibility = View.GONE
        binding.btnHome.visibility = View.VISIBLE
    }

    private fun showHourlyForecast() {
        binding.hourlyForecastContainer.visibility = View.VISIBLE
        binding.nextForecastContainer.visibility = View.GONE
        binding.btnNextForecast.visibility = View.VISIBLE
        binding.btnHome.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherData: FiveDayThreeHourForecastResponse) {
        binding.tvLocation.text = weatherData.city.name

        val currentDate = getCurrentDate()
        binding.tvDate.text = currentDate

        val firstForecast = weatherData.forecastList.firstOrNull()
        if (firstForecast != null) {
            binding.tvTemperature.text = "${firstForecast.main.temp.toInt()}°"
            val description = firstForecast.weather.firstOrNull()?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "No data"
            binding.tvDescription.text = description

            val iconCode = firstForecast.weather.firstOrNull()?.icon
            if (!iconCode.isNullOrEmpty()) {
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                Glide.with(this)
                    .load(iconUrl)
                    .into(binding.iconWeather)
            }
        }

        val minTemp = weatherData.forecastList.minByOrNull { it.main.tempMin }?.main?.tempMin
        val maxTemp = weatherData.forecastList.maxByOrNull { it.main.tempMax }?.main?.tempMax
        if (minTemp != null && maxTemp != null) {
            binding.tvTempRange.text = "Max: ${maxTemp.toInt()}°, Min: ${minTemp.toInt()}°"
        }

        if (firstForecast != null) {
            val precipitationProbability = (firstForecast.pop * 100).toInt()
            binding.tvPrecipitation.text = "$precipitationProbability%"
            binding.tvHumidity.text = "${firstForecast.main.humidity}%"
            binding.tvWindSpeed.text = "${firstForecast.wind.speed} km/h"
        }

        setupHourlyForecast(weatherData.forecastList.take(8))
        setupDailyForecast(weatherData.forecastList)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MMM, d", Locale.ENGLISH)
        val currentDate = System.currentTimeMillis()
        return dateFormat.format(currentDate)
    }

    private fun setupHourlyForecast(forecastList: List<ForecastItem>) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourlyForecast.layoutManager = layoutManager

        val adapter = HourlyForecastAdapter(forecastList)
        binding.rvHourlyForecast.adapter = adapter

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        var closestPosition = 0
        var closestHourDifference = Int.MAX_VALUE
        forecastList.forEachIndexed { index, forecast ->
            val forecastHour = forecast.dateTimeText.split(" ")[1].substringBefore(":").toInt()
            val hourDifference = kotlin.math.abs(currentHour - forecastHour)

            if (hourDifference < closestHourDifference) {
                closestHourDifference = hourDifference
                closestPosition = index
            }
        }

        binding.rvHourlyForecast.post {
            layoutManager.scrollToPositionWithOffset(closestPosition, 100)
        }
    }

    private fun setupDailyForecast(forecastList: List<ForecastItem>) {
        val dailyForecasts = forecastList.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date(it.dt * 1000L))
        }.map { entry ->
            entry.value.first()
        }

        val adapter = DailyForecastAdapter(dailyForecasts)
        binding.rvDailyForecast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDailyForecast.adapter = adapter
    }

    private fun navigateToLocationFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToLocationFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
