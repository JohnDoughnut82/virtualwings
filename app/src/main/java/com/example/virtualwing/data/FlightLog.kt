package com.example.virtualwing.data

data class FlightLog(
    val aircraft: String = "",
    val flightHours: Int = 0,
    val missionType: String = "",
    val date: Long = System.currentTimeMillis()
)
