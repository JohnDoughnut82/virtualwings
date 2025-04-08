package com.example.virtualwing.ui.flightLog

import com.example.virtualwing.BaseActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.virtualwing.R
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.repository.FirebaseService
import com.example.virtualwing.repository.UserRepository
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.flightLog.FlightLogViewModel
import com.example.virtualwing.viewmodel.flightLog.FlightLogViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class FlightLogActivity : BaseActivity() {

    private lateinit var aircraftEditText: EditText
    private lateinit var flightHoursEditText: EditText
    private lateinit var missionTypeEditText: EditText
    private lateinit var saveLogButton: Button

    private val flightLogViewModel: FlightLogViewModel by viewModels {
        ViewModelFactoryProvider.provideFlightLogViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_log)

        setUpDrawer()

        aircraftEditText = findViewById(R.id.aircraftEditText)
        flightHoursEditText = findViewById(R.id.flightHoursEditText)
        missionTypeEditText = findViewById(R.id.missionTypeEditText)
        saveLogButton = findViewById(R.id.saveLogButton)

        saveLogButton.setOnClickListener {
            saveFlightLog()
        }

        observeViewModel()
    }

    private fun saveFlightLog() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val aircraft = aircraftEditText.text.toString()
            val flightHours = flightHoursEditText.text.toString().toIntOrNull() ?: 0
            val missionType = missionTypeEditText.text.toString()

            val flightLog = FlightLog(aircraft, flightHours, missionType)

            flightLogViewModel.saveFlightLog(userId, flightLog)
        }
    }

    private fun observeViewModel() {
        flightLogViewModel.saveSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Flight log saved successfully", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
        })

        flightLogViewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearInputs() {
        aircraftEditText.text.clear()
        flightHoursEditText.text.clear()
        missionTypeEditText.text.clear()
    }
}