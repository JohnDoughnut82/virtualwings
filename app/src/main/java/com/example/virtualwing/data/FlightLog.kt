package com.example.virtualwing.data

data class FlightLog(
    val id: String ="",
    val aircraft: String ="",
    val missionType: String = "",
    val flightHours: Int = 0,
    val wingmen: String = "",
    val date: Long = System.currentTimeMillis(),
    val kills: Int? = null,               // DCS
    val death: Boolean? = null,           // DCS
    val origin: String? = null,
    val destination: String? = null,
    val notes: String? = null
)