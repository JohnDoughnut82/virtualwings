package com.example.virtualwing.ui.home

import com.example.virtualwing.BaseActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.virtualwing.R
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.homeView.HomeViewModel

class HomeActivity : BaseActivity() {

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactoryProvider.provideHomeViewModelFactory()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpDrawer()

        homeViewModel.flightHours.observe(this, Observer { flightHours ->
            val flightViewText = findViewById<TextView>(R.id.flightHoursText)
            flightViewText.text = getString(R.string.flight_hours, flightHours)
        })

        homeViewModel.userName.observe(this, Observer { userName ->
            val welcomeTextView = findViewById<TextView>(R.id.welcomeText)
            welcomeTextView.text = getString(R.string.welcome, userName)
        })

        homeViewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            homeViewModel.logout()
            navigationManager.navigateToLogin()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refreshFlightHours()
    }
}