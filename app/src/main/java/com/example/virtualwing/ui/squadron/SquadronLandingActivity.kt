package com.example.virtualwing.ui.squadron

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel
import com.google.firebase.auth.FirebaseAuth

class SquadronLandingActivity : BaseActivity() {

    private val squadronViewModel: SquadronViewModel by viewModels {
        ViewModelFactoryProvider.provideSquadronViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_squadron_landing)
        setUpDrawer()

        val createBtn = findViewById<Button>(R.id.btn_create_squadron)
        val joinBtn = findViewById<Button>(R.id.btn_join_squadron)

        createBtn.setOnClickListener {
            startActivity(Intent(this, SquadronCreateActivity::class.java))
        }

        joinBtn.setOnClickListener {
            startActivity(Intent(this, SquadronJoinActivity::class.java))
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        squadronViewModel.userSquadron.observe(this) { squadron ->
            if (squadron != null) {
                startActivity(Intent(this, SquadronInfoActivity::class.java))
                finish()
            }
        }

        squadronViewModel.checkUserSquadron(userId)
    }
}