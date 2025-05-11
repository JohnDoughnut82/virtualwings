package com.example.virtualwing.viewmodel.factoryProvider

import androidx.lifecycle.ViewModelProvider
import com.example.virtualwing.repository.FlightLogRepository
import com.example.virtualwing.repository.FlightLogService
import com.example.virtualwing.repository.SquadronRepository
import com.example.virtualwing.repository.SquadronService
import com.example.virtualwing.repository.UserService
import com.example.virtualwing.repository.UserRepository
import com.example.virtualwing.viewmodel.auth.AuthViewModelFactory
import com.example.virtualwing.viewmodel.flightLog.FlightLogViewModelFactory
import com.example.virtualwing.viewmodel.homeView.HomeViewModelFactory
import com.example.virtualwing.viewmodel.profileView.ProfileViewModelFactory
import com.example.virtualwing.viewmodel.squadron.SquadronViewModelFactory

object ViewModelFactoryProvider {
    private val userService by lazy { UserService() }
    private val userRepository by lazy { UserRepository(userService)}
    private val flightLogService by lazy { FlightLogService() }
    private val flightLogRepository by lazy { FlightLogRepository(flightLogService) }
    private val squadronService by lazy { SquadronService() }
    private val squadronRepository by lazy { SquadronRepository(squadronService) }

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
        return FlightLogViewModelFactory(flightLogRepository, userRepository)
    }

    fun provideSquadronViewModelFactory(): ViewModelProvider.Factory {
        return SquadronViewModelFactory(squadronRepository)
    }
}