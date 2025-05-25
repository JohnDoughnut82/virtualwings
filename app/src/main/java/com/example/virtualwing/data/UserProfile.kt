package com.example.virtualwing.data

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val favouriteAircraft: List<String> = emptyList(),
    val totalFlightHours: Int = 0,
    val squadronId: String? = null,
    val isSquadronCreator: Boolean = false
)
