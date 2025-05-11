package com.example.virtualwing.ui.userProfile

import com.example.virtualwing.BaseActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.virtualwing.R
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.google.firebase.auth.FirebaseAuth
import com.example.virtualwing.viewmodel.profileView.ProfileViewModel

class ProfileActivity : BaseActivity() {

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactoryProvider.provideProfileViewModelFactory()
    }

    private lateinit var nameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var aircraftEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setUpDrawer()

        nameEditText = findViewById(R.id.nameEditText)
        bioEditText = findViewById(R.id.bioEditText)
        aircraftEditText = findViewById(R.id.aircraftEditText)
        saveButton = findViewById(R.id.saveButton)

        observeUserProfile()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            profileViewModel.loadUserProfile(it)
        }

        saveButton.setOnClickListener {
            saveProfileChanges()
        }

        nameEditText.addTextChangedListener { setViewModified(true) }
        bioEditText.addTextChangedListener { setViewModified(true) }
        aircraftEditText.addTextChangedListener { setViewModified(true) }
    }

    private fun observeUserProfile() {
        profileViewModel.userProfile.observe(this, Observer { profile ->
            nameEditText.setText(profile.name)
            bioEditText.setText(profile.bio)
            aircraftEditText.setText(profile.favouriteAircraft.joinToString(", "))
        })

        profileViewModel.errorMessage.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }


    private fun saveProfileChanges() {
        val updatedName = nameEditText.text.toString()
        val updatedBio = bioEditText.text.toString()
        val updatedAircraft = aircraftEditText.text.toString().split(",").map { it.trim() }


        profileViewModel.userProfile.value?.let { currentProfile ->
            val updatedProfile = UserProfile(
                name = updatedName,
                email = currentProfile.email,
                bio = updatedBio,
                favouriteAircraft = updatedAircraft,
                totalFlightHours = currentProfile.totalFlightHours,
            )

            FirebaseAuth.getInstance().currentUser?.uid?.let {
                profileViewModel.saveProfileChanges(it, updatedProfile)
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                setViewModified(false)
            }
        }
    }
}