package com.example.virtualwing.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.R
import com.example.virtualwing.data.FlightLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FlightLogAdapter(private var logs: List<FlightLog>) : RecyclerView.Adapter<FlightLogAdapter.FlightLogViewHolder>() {

    inner class FlightLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val aircraft: TextView = itemView.findViewById(R.id.tvAircraft)
        val hours: TextView = itemView.findViewById(R.id.tvHours)
        val mission: TextView = itemView.findViewById(R.id.tvMissionType)
        val wingmen: TextView = itemView.findViewById(R.id.tvWingmen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight_log, parent, false)
        return FlightLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightLogViewHolder, position: Int) {
        val log = logs[position]
        val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(Date(log.date))

        holder.date.text = formattedDate
        holder.aircraft.text = "Aircraft: ${log.aircraft}"
        holder.hours.text = "Hours: ${log.flightHours}"
        holder.mission.text = "Mission: ${log.missionType}"
        holder.wingmen.text = "Wingmen: ${log.wingmen}"
    }

    override fun getItemCount(): Int = logs.size

    fun updateLogs(newLogs: List<FlightLog>) {
        logs = newLogs
        notifyDataSetChanged()
    }
}