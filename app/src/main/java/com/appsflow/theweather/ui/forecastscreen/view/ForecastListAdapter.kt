package com.appsflow.theweather.ui.forecastscreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.appsflow.theweather.data.model.extra.forecast.Daily
import com.appsflow.theweather.databinding.ForecastListItemBinding
import java.text.SimpleDateFormat
import java.util.*

class ForecastListAdapter(private val dailyForecastList: List<Daily>) :
    RecyclerView.Adapter<ForecastListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ViewHolder =
        ViewHolder(
            ForecastListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dailyForecastList[position])
    }

    override fun getItemCount(): Int = dailyForecastList.size

    class ViewHolder(private val binding: ForecastListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Daily) = with(binding) {
            tvDate.text = SimpleDateFormat(
                "dd/MM/yyyy", Locale.ENGLISH
            ).format(Date(item.dt.toLong() * 1000))
            tvStatus.text = item.weather[0].description.replaceFirstChar { it.uppercase() }
            tvMinTemp.text = item.temp.min.toString().substringBefore(".") + "°C"
            tvMaxTemp.text = item.temp.max.toString().substringBefore(".") + "°C"
            binding.root.setOnClickListener {
                Toast.makeText(
                    binding.root.context,
                    "Click",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}