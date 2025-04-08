package com.example.virtualwing.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _authenticated = MutableLiveData<Boolean>()
    val authenticated: LiveData<Boolean> get() = _authenticated

    private val _authError = MutableLiveData<String>()
    val authError: LiveData<String> get() = _authError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            if(result.isSuccess) {
                _authenticated.postValue(true)
            } else {
                _authError.postValue("Login failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        bio: String?,
        favouriteAircraft: List<String>?
    ) {
        viewModelScope.launch {
            val userProfile = UserProfile(
                name = name,
                email = email,
                bio = bio ?: "",
                favouriteAircraft = favouriteAircraft ?: listOf(),
                totalFlightHours = 0
            )
            val result = userRepository.signUp(email, password, userProfile)
            if(result.isSuccess) {
                _authenticated.postValue(true)
            } else {
                _authError.postValue("Sign up failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}