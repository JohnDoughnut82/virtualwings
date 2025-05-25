package com.example.virtualwing.ui.squadron

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.utils.SquadronAdapter
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel

class SquadronJoinActivity : BaseActivity() {

    private lateinit var squadronRecyclerView: RecyclerView
    private lateinit var adapter: SquadronAdapter

    private val squadronViewModel: SquadronViewModel by viewModels {
        ViewModelFactoryProvider.provideSquadronViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_squadron_join)
        setUpDrawer()

        squadronRecyclerView = findViewById(R.id.rv_squadron_list)
        squadronRecyclerView.layoutManager = LinearLayoutManager(this)

        squadronViewModel.getAllSquadrons().observe(this) { squadrons ->
            if (squadrons.isNullOrEmpty()) {
                Toast.makeText(this, "No Squadrons Available.", Toast.LENGTH_SHORT).show()
                squadronRecyclerView.visibility = View.GONE
            } else {
                adapter = SquadronAdapter(squadrons, ::onSquadronSelected)
                squadronRecyclerView.adapter = adapter
                squadronRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun onSquadronSelected(squadron: Squadron) {
        showSquadronDetailDialog(squadron)
    }

    private fun showSquadronDetailDialog(squadron: Squadron) {
        val dialog = SquadronDetailDialog(this, squadron) { selectedSquadron ->
            squadronViewModel.sendJoinRequest(selectedSquadron.id)
            Toast.makeText(this, "Join request sent to ${selectedSquadron.name}", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
        squadronViewModel.joinRequestResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Join request sent!", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(this, "Failed to send request: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}