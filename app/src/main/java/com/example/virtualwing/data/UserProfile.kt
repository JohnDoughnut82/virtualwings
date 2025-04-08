package com.example.virtualwing.data

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val favouriteAircraft: List<String> = emptyList(),
    val totalFlightHours: Int = 0
)
