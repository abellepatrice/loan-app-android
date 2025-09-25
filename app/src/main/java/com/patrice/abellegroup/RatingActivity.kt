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
import com.patrice.abellegroup.models.FeedbackRequest
import com.patrice.abellegroup.models.FeedbackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatingActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_rating)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etFeedback = findViewById<EditText>(R.id.etFeedback)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val feedback = etFeedback.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(this, "Please provide a star rating.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = sharedPref.getString("authToken", "") ?: ""
            if (token.isEmpty()) {
                Toast.makeText(this, "Session expired, please log in again.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@setOnClickListener
            }

            submitFeedback(token, rating, feedback)
        }
    }

    private fun submitFeedback(token: String, rating: Float, feedback: String) {
        val request = FeedbackRequest(rating = rating, feedback = feedback)

        RetrofitClient.apiService.submitFeedback("Bearer $token", request)
            .enqueue(object : Callback<FeedbackResponse> {
                override fun onResponse(
                    call: Call<FeedbackResponse>,
                    response: Response<FeedbackResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@RatingActivity,
                            "Thanks for your feedback!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RatingActivity,
                            "Failed to submit feedback",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RatingActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
