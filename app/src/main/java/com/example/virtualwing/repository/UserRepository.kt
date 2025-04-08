package com.example.virtualwing.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.data.UserProfile

class UserRepository(private val firebaseService: FirebaseService) {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getCurrentUserId(): String? {
        return firebaseService.getCurrentUserId()
    }

    fun getCurrentUserEmail(): String? {
        return firebaseService.getCurrentUserEmail()
    }

    suspend fun getUserNameFromProfile(userId: String): String? {
        return firebaseService.getUserNameFromProfile(userId)
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        val profile = firebaseService.getUserProfile(userId)
        if (profile == null) {
            _errorMessage.postValue("User profile not found.")
        }
        return profile
    }

    suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile): Boolean {
        val result = firebaseService.updateUserProfile(userId, updatedProfile)
        if (!result) {
            _errorMessage.postValue("Error updating profile.")
        }
        return result
    }

    suspend fun saveFlightLog(userId: String, flightLog: FlightLog): Boolean {
        val result = firebaseService.saveFlightLog(userId, flightLog)
        if(!result) {
            _errorMessage.postValue("Error saving flight log.")
        }
        return result
    }

    suspend fun getUserFlightHours(userId: String): Int {
        return firebaseService.getUserFlightHours(userId)
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return firebaseService.login(email, password)
    }

    suspend fun signUp(
        email: String,
        password: String,
        userProfile: UserProfile
    ): Result<Unit> {
        return firebaseService.signUpUser(email, password, userProfile)
    }
}
