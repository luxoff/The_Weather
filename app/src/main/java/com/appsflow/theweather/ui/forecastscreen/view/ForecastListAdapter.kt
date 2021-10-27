package com.appsflow.theweather.ui.forecastscreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.appsflow.theweather.data.model.ForecastWeatherInfo
import com.appsflow.theweather.databinding.ForecastListItemBinding

class ForecastListAdapter(private val mutableForecastList: MutableList<ForecastWeatherInfo>) :
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
        holder.bind(mutableForecastList[position])
    }

    override fun getItemCount(): Int = mutableForecastList.size

    class ViewHolder(private val binding: ForecastListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ForecastWeatherInfo) = with(binding) {
            tvDate.text = item.datetime
            tvStatus.text = item.status
            tvMinTemp.text = item.minTemp
            tvMaxTemp.text = item.maxTemp
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