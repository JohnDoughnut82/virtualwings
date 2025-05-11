package com.example.virtualwing.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.virtualwing.data.UserProfile

class UserRepository(private val userService: UserService) {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getCurrentUserId(): String? {
        return userService.getCurrentUserId()
    }

    fun getCurrentUserEmail(): String? {
        return userService.getCurrentUserEmail()
    }

    suspend fun getUserNameFromProfile(userId: String): String? {
        return userService.getUserNameFromProfile(userId)
    }


    suspend fun getUserProfile(userId: String): UserProfile? {
        val profile = userService.getUserProfile(userId)
        if (profile == null) {
            _errorMessage.postValue("User profile not found.")
        }
        return profile
    }

    suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile): Boolean {
        val result = userService.updateUserProfile(userId, updatedProfile)
        if (!result) {
            _errorMessage.postValue("Error updating profile.")
        }
        return result
    }

    suspend fun getUserFlightHours(userId: String): Int {
        return userService.getUserFlightHours(userId)
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return userService.login(email, password)
    }

    suspend fun signUp(
        email: String,
        password: String,
        userProfile: UserProfile
    ): Result<Unit> {
        return userService.signUpUser(email, password, userProfile)
    }
}
