package com.example.virtualwing.ui.flightLog

import com.example.virtualwing.BaseActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.virtualwing.R
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.flightLog.FlightLogViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class FlightLogActivity : BaseActivity() {


    private lateinit var aircraftEditText: EditText
    private lateinit var flightHoursEditText: EditText
    private lateinit var missionTypeEditText: EditText
    private lateinit var wingmenEditText: EditText
    private lateinit var missionDateEdit: DatePicker
    private lateinit var saveLogButton: Button
    private lateinit var viewLogsButton: Button

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
        wingmenEditText = findViewById(R.id.wingmenEditText)
        missionDateEdit = findViewById(R.id.missionDateEdit)
        saveLogButton = findViewById(R.id.saveLogButton)
        viewLogsButton = findViewById(R.id.viewExistingLogs)

        saveLogButton.setOnClickListener {
            saveFlightLog()
        }

        viewLogsButton.setOnClickListener {
            navigateToViewLogs()
        }

        observeViewModel()

        aircraftEditText.addTextChangedListener { setViewModified(true) }
        flightHoursEditText.addTextChangedListener { setViewModified(true) }
        missionTypeEditText.addTextChangedListener { setViewModified(true) }
        wingmenEditText.addTextChangedListener { setViewModified(true) }
    }

    private fun observeViewModel() {

        flightLogViewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        flightLogViewModel.totalFlightHours.observe(this, Observer { totalHours ->
            Toast.makeText(this, "Total flight hours: $totalHours", Toast.LENGTH_SHORT).show()
        })

        flightLogViewModel.saveSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Flight log saved successfully", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
        }
    }

    private fun saveFlightLog() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val aircraft = aircraftEditText.text.toString()
            val flightHours = flightHoursEditText.text.toString().toIntOrNull() ?: 0
            val missionType = missionTypeEditText.text.toString()
            val wingmen = wingmenEditText.text.toString()
            val missionDate = Calendar.getInstance()
            missionDate.set(
                missionDateEdit.year,
                missionDateEdit.month,
                missionDateEdit.dayOfMonth
            )
            val missionDateMillis = missionDate.timeInMillis

            val flightLog = FlightLog(
                id = "",
                aircraft = aircraft,
                missionType = missionType,
                flightHours = flightHours,
                wingmen = wingmen,
                date = missionDateMillis
            )

            flightLogViewModel.saveFlightLog(userId, flightLog)
        }
    }

    private fun navigateToViewLogs() {
        navigationManager.navigateToViewFlightLogs()
    }

    private fun clearInputs() {
        aircraftEditText.text.clear()
        flightHoursEditText.text.clear()
        missionTypeEditText.text.clear()
        wingmenEditText.text.clear()

        val today = Calendar.getInstance()
        missionDateEdit.updateDate(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
    }
}