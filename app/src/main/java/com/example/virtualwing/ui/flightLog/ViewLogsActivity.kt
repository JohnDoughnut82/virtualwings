package com.example.virtualwing.ui.flightLog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.utils.FlightLogAdapter
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.flightLog.FlightLogViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewLogsActivity : BaseActivity() {

    private lateinit var adapter: FlightLogAdapter
    private val flightLogViewModel: FlightLogViewModel by viewModels {
        ViewModelFactoryProvider.provideFlightLogViewModelFactory()
    }

    private var currentLogs: List<FlightLog> = emptyList()
    private var currentSortField: String = "Date"
    private var isAscending: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_logs)

        setUpDrawer()

        adapter = FlightLogAdapter(emptyList())
        val recyclerView = findViewById<RecyclerView>(R.id.logs_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        flightLogViewModel.flightLogs.observe(this) { logs ->
            adapter.updateLogs(logs)
            currentLogs = logs
            applyFilterAndSorting()
        }

        val searchInput = findViewById<EditText>(R.id.search_input)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applyFilterAndSorting()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val sortSpinner = findViewById<Spinner>(R.id.sort_spinner)
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                currentSortField = parent.getItemAtPosition(position).toString()
                applyFilterAndSorting()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val sortToggle = findViewById<ImageButton>(R.id.sort_direction_toggle)
        sortToggle.setOnClickListener {
            isAscending = !isAscending
            updateSortIcon(sortToggle)
            applyFilterAndSorting()
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            flightLogViewModel.loadFlightLogs(userId)
        }

        updateSortIcon(sortToggle)
    }

    private fun updateSortIcon(button: ImageButton) {
        val iconRes = if (!isAscending) R.drawable.ic_sort_up else R.drawable.ic_sort_down
        button.setImageResource(iconRes)
    }

    private fun applyFilterAndSorting() {
        val query = findViewById<EditText>(R.id.search_input).text.toString().lowercase()

        var filtered = currentLogs.filter {
            it.aircraft.lowercase().contains(query) ||
            it.missionType.lowercase().contains(query) ||
            it.wingmen.lowercase().contains(query) ||
            it.flightHours.toString().contains(query) ||
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.date)).lowercase().contains(query)
        }

        filtered = when (currentSortField) {
            "Date" -> if(isAscending) filtered.sortedBy { it.date } else filtered.sortedByDescending { it.date }
            "Hours" -> if(isAscending) filtered.sortedBy { it.flightHours } else filtered.sortedByDescending { it.flightHours }
            "Aircraft" -> if(isAscending) filtered.sortedBy { it.aircraft } else filtered.sortedByDescending { it.aircraft }
            "Mission" -> if(isAscending) filtered.sortedBy { it.missionType } else filtered.sortedByDescending { it.missionType }
            "Wingmen" -> if(isAscending) filtered.sortedBy { it.wingmen } else filtered.sortedByDescending { it.wingmen }
            else -> filtered
        }

        adapter.updateLogs(filtered)
    }
}