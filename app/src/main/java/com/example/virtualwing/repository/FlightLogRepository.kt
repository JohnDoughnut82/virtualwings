package com.example.virtualwing.repository

import com.example.virtualwing.data.FlightLog

class FlightLogRepository(private val flightLogService: FlightLogService) {

    suspend fun saveFlightLog(userId: String, flightLog: FlightLog): Boolean {
        return flightLogService.saveFlightLog(userId, flightLog)
    }

    suspend fun getFlightLogs(userId: String): List<FlightLog> {
        return flightLogService.getFlightLogs(userId)
    }

    suspend fun getTotalFlightHours(userId: String): Int {
        return flightLogService.getTotalFlightHours(userId)
    }
}