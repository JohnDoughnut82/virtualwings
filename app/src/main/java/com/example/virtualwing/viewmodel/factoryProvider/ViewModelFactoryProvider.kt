package com.example.virtualwing.viewmodel.factoryProvider

import androidx.lifecycle.ViewModelProvider
import com.example.virtualwing.repository.FirebaseService
import com.example.virtualwing.repository.UserRepository
import com.example.virtualwing.viewmodel.auth.AuthViewModelFactory
import com.example.virtualwing.viewmodel.flightLog.FlightLogViewModelFactory
import com.example.virtualwing.viewmodel.homeView.HomeViewModelFactory
import com.example.virtualwing.viewmodel.profileView.ProfileViewModelFactory

object ViewModelFactoryProvider {
    private val firebaseService by lazy { FirebaseService() }
    private val userRepository by lazy { UserRepository(firebaseService)}

    fun provideProfileViewModelFactory(): ViewModelProvider.Factory {
        return ProfileViewModelFactory(userRepository)
    }

    fun provideAuthViewModelFactory(): ViewModelProvider.Factory {
        return AuthViewModelFactory(userRepository)
    }

    fun provideHomeViewModelFactory(): ViewModelProvider.Factory {
        return HomeViewModelFactory(userRepository)
    }

    fun provideFlightLogViewModelFactory(): ViewModelProvider.Factory {
        return FlightLogViewModelFactory(userRepository)
    }
}