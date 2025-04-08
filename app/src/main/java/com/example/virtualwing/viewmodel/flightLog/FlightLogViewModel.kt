package com.example.virtualwing.viewmodel.flightLog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.repository.UserRepository
import kotlinx.coroutines.launch

class FlightLogViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveFlightLog(userId: String, flightLog: FlightLog) {
        viewModelScope.launch {
            try {
                userRepository.saveFlightLog(userId, flightLog)
                _saveSuccess.postValue(true)
            } catch (e: Exception) {
                _errorMessage.postValue("Error saving flight log: ${e.message}")
            }
        }
    }
}