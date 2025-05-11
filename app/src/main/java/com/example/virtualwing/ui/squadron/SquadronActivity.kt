package com.example.virtualwing.ui.squadron

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide

class SquadronActivity : BaseActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var regionSpinner: Spinner
    private lateinit var timezoneSpinner: Spinner
    private lateinit var emblemImage: ImageView
    private lateinit var uploadBtn: Button
    private lateinit var createBtn: Button

    private lateinit var leaveBtn: Button
    private lateinit var disbandBtn: Button

    private var emblemUri: Uri? = null

    companion object {
        private const val IMAGE_PICK_CODE = 101
    }

    private val squadronViewModel: SquadronViewModel by viewModels {
        ViewModelFactoryProvider.provideSquadronViewModelFactory()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.squadron_activity)
        setUpDrawer()

        squadronViewModel.userSquadron.observe(this) { squadron ->
            if (squadron == null) {
                findViewById<View>(R.id.create_layout).visibility = View.VISIBLE
                findViewById<View>(R.id.info_layout).visibility = View.GONE
            } else {
                findViewById<View>(R.id.create_layout).visibility = View.GONE
                findViewById<View>(R.id.info_layout).visibility = View.VISIBLE
                renderSquadronInfo(squadron)
            }
        }

        setupViews()
        setupSpinners()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        squadronViewModel.checkUserSquadron(userId)

        squadronViewModel.creationResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Squadron Created!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Creation failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        squadronViewModel.leaveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "You left the squadron.", Toast.LENGTH_SHORT).show()
                squadronViewModel.checkUserSquadron(userId)
            }.onFailure {
                Toast.makeText(this, "Failed to leave: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        squadronViewModel.disbandResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Squadron disbanded.", Toast.LENGTH_SHORT).show()
                squadronViewModel.checkUserSquadron(userId)
            }.onFailure {
                Toast.makeText(this, "Failed to disband: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViews() {
        nameInput = findViewById(R.id.et_squadron_name)
        descInput = findViewById(R.id.et_squadron_description)
        regionSpinner = findViewById(R.id.spinner_region)
        timezoneSpinner = findViewById(R.id.spinner_timezone)
        emblemImage = findViewById(R.id.squad_emblem_preview)
        uploadBtn = findViewById(R.id.btn_upload_emblem)
        createBtn = findViewById(R.id.btn_create_squadron)

        leaveBtn = findViewById(R.id.btn_leave)
        disbandBtn = findViewById(R.id.btn_disband)

        uploadBtn.setOnClickListener {
            pickImageFromGallery()
        }

        createBtn.setOnClickListener {
            createSquadron()
        }

        leaveBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            squadronViewModel.userSquadron.value?.let { squad ->
                squadronViewModel.leaveSquadron(userId, squad.id)
            }
        }

        disbandBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            squadronViewModel.userSquadron.value?.let { squad ->
                squadronViewModel.disbandSquadron(userId, squad.id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupSpinners() {
        val regions = resources.getStringArray(R.array.regions)
        regionSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, regions)

        val timezones = TimeZone.getAvailableIDs().toList().sorted()
        timezoneSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timezones)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            emblemUri = data?.data
            emblemImage.setImageURI(emblemUri)
        }
    }

    private fun createSquadron() {
        val name = nameInput.text.toString().trim()
        val desc = descInput.text.toString().trim()
        val region = regionSpinner.selectedItem.toString()
        val timezone = timezoneSpinner.selectedItem.toString()
        val creatorId = FirebaseAuth.getInstance().currentUser?.uid

        if (name.isEmpty() || desc.isEmpty() || creatorId == null) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        squadronViewModel.createSquadron(
            name, desc, region, timezone, emblemUri, creatorId)
    }

    @SuppressLint("StringFormatMatches")
    private fun renderSquadronInfo(squadron: Squadron) {
        findViewById<TextView>(R.id.tv_squadron_name).text = squadron.name
        findViewById<TextView>(R.id.tv_squadron_desc).text = squadron.description
        findViewById<TextView>(R.id.tv_squadron_region).text = squadron.region
        findViewById<TextView>(R.id.tv_squadron_timezone).text = squadron.timezone
        findViewById<TextView>(R.id.tv_member_count).text =
            getString(R.string.squadron_members, squadron.memberIds.size)

        val isCreator = FirebaseAuth.getInstance().currentUser?.uid == squadron.creatorId
        findViewById<Button>(R.id.btn_leave).visibility = if (isCreator) View.GONE else View.VISIBLE
        findViewById<Button>(R.id.btn_disband).visibility = if (isCreator) View.VISIBLE else View.GONE

        Glide.with(this).load(squadron.emblemUrl).into(findViewById(R.id.iv_squad_emblem))
    }
}