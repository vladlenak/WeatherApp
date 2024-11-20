package t.me.octopusapps.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import t.me.octopusapps.weatherapp.R
import t.me.octopusapps.weatherapp.data.model.ForecastItem
import t.me.octopusapps.weatherapp.databinding.ItemHourlyForecastBinding
import java.util.Calendar
import kotlin.math.abs

class HourlyForecastAdapter(
    private val forecastList: List<ForecastItem>
) : RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val binding = ItemHourlyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourlyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        holder.bind(forecastList[position])
    }

    override fun getItemCount(): Int = forecastList.size

    inner class HourlyForecastViewHolder(private val binding: ItemHourlyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(forecast: ForecastItem) {
            binding.tvTemperature.text = "${forecast.main.temp.toInt()}°"

            val iconUrl =
                "https://openweathermap.org/img/wn/${forecast.weather.firstOrNull()?.icon}@2x.png"
            Glide.with(binding.root.context)
                .load(iconUrl)
                .into(binding.iconWeather)

            val forecastTime = forecast.dateTimeText.split(" ")[1].substringBeforeLast(":")
            binding.tvTime.text = forecastTime

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val forecastHour = forecastTime.substringBefore(":").toInt()

            val isNearestInterval = isNearestTimeInterval(currentHour, forecastHour)

            if (isNearestInterval) {
                binding.root.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.selected_item_background
                )
            } else {
                binding.root.background = null
            }
        }


        private fun isNearestTimeInterval(currentHour: Int, forecastHour: Int): Boolean {
            val interval = 3
            val difference = abs(currentHour - forecastHour)

            return difference < interval && (currentHour / interval == forecastHour / interval)
        }

    }
}
