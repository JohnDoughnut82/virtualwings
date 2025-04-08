package com.example.virtualwing.ui.login

import android.content.Intent
import com.example.virtualwing.BaseActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.virtualwing.R
import com.example.virtualwing.ui.signup.SignUpActivity
import com.example.virtualwing.viewmodel.auth.AuthViewModel
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider

class LoginActivity : BaseActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactoryProvider.provideAuthViewModelFactory()
    }

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToSignUp: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp)


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        observeAuthState()
    }

    private fun observeAuthState() {
        authViewModel.authenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                navigationManager.navigateToHome()
                finish()
            }
        }

        authViewModel.authError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}
