package com.example.virtualwing.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.ui.login.LoginActivity
import com.example.virtualwing.viewmodel.auth.AuthViewModel
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider

class SignUpActivity : BaseActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactoryProvider.provideAuthViewModelFactory()
    }

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etBio: EditText
    private lateinit var etAircraft: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnBackToLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etBio = findViewById(R.id.etBio)
        etAircraft = findViewById(R.id.etAircraft)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val bio = etBio.text.toString().trim()
            val aircraft = etAircraft.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.signUp(name, email, password, bio, aircraft)
        }

        btnBackToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        observeAuthState()
    }

    private fun observeAuthState() {
        authViewModel.authenticated.observe(this) { isAuthenticated ->
            if(isAuthenticated) {
                navigationManager.navigateToHome()
                finish()
            }
        }

        authViewModel.authError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}