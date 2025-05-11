package com.example.virtualwing.viewmodel.flightLog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualwing.repository.FlightLogRepository
import com.example.virtualwing.repository.UserRepository

class FlightLogViewModelFactory(
    private val flightLogRepository: FlightLogRepository,
    private val userRepository: UserRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightLogViewModel::class.java)) {
            return FlightLogViewModel(flightLogRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}