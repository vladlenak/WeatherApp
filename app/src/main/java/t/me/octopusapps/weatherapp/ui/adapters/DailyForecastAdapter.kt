package t.me.octopusapps.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import t.me.octopusapps.weatherapp.data.model.ForecastItem
import t.me.octopusapps.weatherapp.databinding.ItemDailyForecastBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyForecastAdapter(private val dailyForecasts: List<ForecastItem>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    class DailyForecastViewHolder(val binding: ItemDailyForecastBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = ItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyForecastViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val forecast = dailyForecasts[position]
        val binding = holder.binding

        val sdf = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val date = Date(forecast.dt * 1000L)
        binding.tvDay.text = sdf.format(date)

        val iconCode = forecast.weather.firstOrNull()?.icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        Glide.with(binding.iconWeather.context)
            .load(iconUrl)
            .into(binding.iconWeather)

        binding.tvMaxTemp.text = "${forecast.main.tempMax.toInt()}°"
        binding.tvMinTemp.text = "${forecast.main.tempMin.toInt()}°"
    }

    override fun getItemCount(): Int = dailyForecasts.size
}

