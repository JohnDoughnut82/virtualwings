package com.example.virtualwing.viewmodel.homeView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.repository.FlightLogRepository
import com.example.virtualwing.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _flightHours = MutableLiveData<Int>()
    val flightHours: LiveData<Int> get() = _flightHours

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    val userId = userRepository.getCurrentUserId()

    init {
        val email = userRepository.getCurrentUserEmail()
        _userEmail.value = email ?: "User has no email"
        loadUserInfo()
        refreshFlightHours()
    }

    private fun loadUserInfo() {
        userId?.let {
            viewModelScope.launch {
                val userName = userRepository.getUserNameFromProfile(it)
                _userName.value = userName ?: "Could not get user name"
            }
        }
    }

    fun refreshFlightHours() {
        userId?.let {
            viewModelScope.launch {
                val hours = userRepository.getUserFlightHours(it)
                _flightHours.value = hours
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}
