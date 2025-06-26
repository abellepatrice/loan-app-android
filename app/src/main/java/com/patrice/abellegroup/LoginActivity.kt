package com.patrice.abellegroup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.patrice.abellegroup.api.RetrofitClient
import com.patrice.abellegroup.models.LoginRequest
import com.patrice.abellegroup.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView
    private lateinit var tvError: TextView

    private val sharedPref by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "LoginPrefs",
                masterKeyAlias,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignup = findViewById(R.id.tvSignup)
        tvError = findViewById(R.id.tvError)

        // Autofill saved email
        val savedUsername = sharedPref.getString("userEmail", "")
        etUsername.setText(savedUsername)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                tvError.text = "All fields are required."
                return@setOnClickListener
            }

            tvError.text = "" // Clear any previous error

            val loginRequest = LoginRequest(username, password)

            RetrofitClient.apiService.login(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val loginResponse = response.body()
                            with(sharedPref.edit()) {
                                putBoolean("isLoggedIn", true)
                                putString("userEmail", loginResponse?.user?.email ?: username)
                                putString("authToken", loginResponse?.token ?: "")
                                putString("userUsername", loginResponse?.user?.username ?: username)

                                apply()
                            }
                            Toast.makeText(this@LoginActivity, "Welcome, ${loginResponse?.user?.username}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                            finish()
                        } else {
                            tvError.text = "Invalid username or password."
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        tvError.text = "Network error: ${t.message}"
                    }
                })
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
