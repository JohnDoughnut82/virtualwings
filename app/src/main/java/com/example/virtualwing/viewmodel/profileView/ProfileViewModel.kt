package com.example.virtualwing.viewmodel.profileView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _checkedState = MutableLiveData<BooleanArray>()
    val checkedState: LiveData<BooleanArray> get() = _checkedState


    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            userRepository.getUserProfile(userId)?.let { profile ->
                _userProfile.postValue(profile)
            } ?: run {
                _errorMessage.postValue("User profile not found.")
            }
        }
    }

    fun saveProfileChanges(userId: String, updatedProfile: UserProfile) {
        viewModelScope.launch {
            val success = userRepository.updateUserProfile(userId, updatedProfile)
            if (!success) {
                _errorMessage.postValue("Failed to update profile.")
            }
        }
    }
}