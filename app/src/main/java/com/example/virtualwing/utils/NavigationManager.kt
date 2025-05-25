package com.example.virtualwing.utils

import android.content.Context
import android.content.Intent
import com.example.virtualwing.ui.flightLog.FlightLogActivity
import com.example.virtualwing.ui.flightLog.ViewLogsActivity
import com.example.virtualwing.ui.home.HomeActivity
import com.example.virtualwing.ui.login.LoginActivity
import com.example.virtualwing.ui.squadron.SquadronLandingActivity
import com.example.virtualwing.ui.userProfile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth

class NavigationManager(private val context: Context) {

    fun navigateToHome() {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun navigateToFlightLog() {
        val intent = Intent(context, FlightLogActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToViewFlightLogs() {
        val intent = Intent(context, ViewLogsActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToProfile() {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToSquadron() {
        val intent = Intent(context, SquadronLandingActivity::class.java)
        context.startActivity(intent)
    }

    fun logoutAndNavigateToLogin() {
        FirebaseAuth.getInstance().signOut()
        navigateToLogin()
    }
}