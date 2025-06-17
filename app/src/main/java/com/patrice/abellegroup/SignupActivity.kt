package com.patrice.abellegroup

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.patrice.abellegroup.api.RetrofitClient
import com.patrice.abellegroup.models.SignupRequest
import com.patrice.abellegroup.models.SignupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvError = findViewById(R.id.tvError)

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Name is required"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            tvError.text = "" // Clear error text

            val signupRequest = SignupRequest(name, email, password)

            RetrofitClient.apiService.signup(signupRequest).enqueue(object :
                Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    if (response.isSuccessful) {
                        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("token", response.body()?.token)
                            .putString("role", response.body()?.role)
                            .apply()

                        Toast.makeText(
                            this@SignupActivity,
                            "Signup successful",
                            Toast.LENGTH_SHORT
                        ).show()


                        val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        tvError.text = "Signup failed: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    tvError.text = "Network error: ${t.message}"
                }
            })
        }
    }
}
