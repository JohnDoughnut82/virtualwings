package com.example.virtualwing.ui.squadron

import android.content.Intent
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class SquadronCreateActivity : BaseActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var regionSpinner: Spinner
    private lateinit var timezoneSpinner: Spinner
    private lateinit var emblemImage: ImageView
    private lateinit var uploadIcon: ImageView
    private lateinit var createButton: Button

    private var emblemUri: Uri? = null

    private val squadronViewModel: SquadronViewModel by viewModels {
        ViewModelFactoryProvider.provideSquadronViewModelFactory()
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        emblemUri = uri
        if (emblemUri != null) {
            emblemImage.setImageURI(emblemUri)
            emblemImage.visibility = View.VISIBLE
            uploadIcon.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_squadron_create)
        setUpDrawer()

        nameInput = findViewById(R.id.et_squadron_name)
        descInput = findViewById(R.id.et_squadron_description)
        regionSpinner = findViewById(R.id.spinner_region)
        timezoneSpinner = findViewById(R.id.spinner_timezone)
        emblemImage = findViewById(R.id.iv_emblem)
        uploadIcon = findViewById(R.id.iv_upload_icon)
        createButton = findViewById(R.id.btn_create)

        setupSpinners()

        uploadIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        createButton.setOnClickListener {
            createSquadron()
        }

        squadronViewModel.creationResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Squadron Created!", Toast.LENGTH_SHORT).show()
                finish() // or navigate to SquadronInfoActivity
            }.onFailure {
                Toast.makeText(this, "Creation failed: ${it.message}", Toast.LENGTH_SHORT).show()
                createButton.isEnabled = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupSpinners() {
        val regions = resources.getStringArray(R.array.regions)
        regionSpinner.adapter = ArrayAdapter(this, R.layout.spinner_drowpdown_item, regions)

        val timezones = TimeZone.getAvailableIDs().toList().sorted()
        timezoneSpinner.adapter = ArrayAdapter(this, R.layout.spinner_drowpdown_item, timezones)
    }

    private fun createSquadron() {
        val name = nameInput.text.toString().trim()
        val desc = descInput.text.toString().trim()
        val region = regionSpinner.selectedItem?.toString() ?: ""
        val timeZone = timezoneSpinner.selectedItem?.toString() ?: ""
        val creatorId = FirebaseAuth.getInstance().currentUser?.uid

        if (name.isEmpty() || desc.isEmpty() || creatorId == null) {
            Toast.makeText(this, "Please complete all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        createButton.isEnabled = false

        squadronViewModel.createSquadron(
            name, desc, region, timeZone, emblemUri, creatorId
        )

        squadronViewModel.userSquadron.observe(this) { squadron ->
            if (squadron != null) {
                startActivity(Intent(this, SquadronInfoActivity::class.java))
                finish()
            }
        }
    }
}