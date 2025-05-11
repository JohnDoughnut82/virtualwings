package com.example.virtualwing.viewmodel.flightLog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.repository.FlightLogRepository
import com.example.virtualwing.repository.UserRepository
import kotlinx.coroutines.launch
import kotlin.math.log

class FlightLogViewModel(
    private val flightLogRepository: FlightLogRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _flightLogs = MutableLiveData<List<FlightLog>>()
    val flightLogs: LiveData<List<FlightLog>> get() = _flightLogs

    private val _totalFlightHours = MutableLiveData<Int>()
    val totalFlightHours: LiveData<Int> get() = _totalFlightHours

    private val _flightSims = MutableLiveData<List<String>>()
    val flightSims: LiveData<List<String>> get() = _flightSims

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess

    fun saveFlightLog(userId: String, flightLog: FlightLog) {

        if (!validateFlightLog(flightLog)) {
            _errorMessage.postValue("Please fill in all the fields correctly.")
            return
        }
        viewModelScope.launch {
            try {
                val success = flightLogRepository.saveFlightLog(userId, flightLog)
                _saveSuccess.postValue(success)

                if (success) {
                    val totalHours = flightLogRepository.getTotalFlightHours(userId)
                    _totalFlightHours.postValue(totalHours)
                } else {
                    _errorMessage.postValue("Failed to save flight log.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error occurred while saving the flight log.")
                _saveSuccess.postValue(false)
            }
        }
    }

    fun loadFlightLogs(userId: String) {
        viewModelScope.launch {
            try {
                val logs = flightLogRepository.getFlightLogs(userId)
                _flightLogs.postValue(logs)
                Log.d("ViewLogs", "Loaded ${logs.size} logs")
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to load flight logs.")
            }
        }
    }

    private fun validateFlightLog(flightLog: FlightLog): Boolean {
        return flightLog.aircraft.isNotEmpty() && flightLog.missionType.isNotEmpty() && flightLog.flightHours > 0 && flightLog.date > 0
    }
}