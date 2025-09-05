package com.patrice.abellegroup

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.patrice.abellegroup.api.RetrofitClient.apiService
import com.patrice.abellegroup.models.LoanRequest
import com.patrice.abellegroup.models.LoanResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplyLoanActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etPurpose: EditText
    private lateinit var etDuration: EditText
    private lateinit var btnApplyLoan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_loan)

        etAmount = findViewById(R.id.etAmount)
        etPurpose = findViewById(R.id.etPurpose)
        etDuration = findViewById(R.id.etDuration)
        btnApplyLoan = findViewById(R.id.btnApplyLoan)

        btnApplyLoan.setOnClickListener {
            applyForLoan()
        }
    }

    private fun applyForLoan() {
        val amountStr = etAmount.text.toString().trim()
        val purpose = etPurpose.text.toString().trim()
        val durationStr = etDuration.text.toString().trim()

        if (amountStr.isEmpty() || purpose.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toIntOrNull()
        val durationMonths = durationStr.toIntOrNull()

        if (amount == null || durationMonths == null) {
            Toast.makeText(this, "Invalid number entered", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve userId from SharedPreferences
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("authToken", null)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val loanRequest = LoanRequest(
            userId = userId,
            amount = amount,
            purpose = purpose,
            durationMonths = durationMonths
        )

        val call = apiService.applyLoan("Bearer $token",loanRequest)
        call.enqueue(object : Callback<LoanResponse> {
            override fun onResponse(call: Call<LoanResponse>, response: Response<LoanResponse>) {
                if (response.isSuccessful) {
                    val loanResponse = response.body()
                    Toast.makeText(
                        this@ApplyLoanActivity,
                        loanResponse?.message ?: "Loan application successful",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@ApplyLoanActivity,
                        "Failed to apply for loan: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoanResponse>, t: Throwable) {
                Toast.makeText(
                    this@ApplyLoanActivity,
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}


//package com.patrice.abellegroup
//
//import android.content.Context
//import android.os.Build
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//import com.patrice.abellegroup.api.RetrofitClient
//import com.patrice.abellegroup.models.LoanRequest
//import com.patrice.abellegroup.models.LoanResponse
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//
//class ApplyLoanActivity : AppCompatActivity() {
//
//    private lateinit var etAmount: EditText
//    private lateinit var etDuration: EditText
//    private lateinit var etPurpose: EditText
//    private lateinit var btnApply: Button
//
//    private val sharedPref by lazy {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//            EncryptedSharedPreferences.create(
//                "LoginPrefs",
//                masterKeyAlias,
//                applicationContext,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//            )
//        } else {
//            getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_apply_loan)
//
//        etAmount = findViewById(R.id.etAmount)
//        etDuration = findViewById(R.id.etDuration)
//        etPurpose = findViewById(R.id.etPurpose)
//        btnApply = findViewById(R.id.btnApplyLoan)
//
//        btnApply.setOnClickListener {
//            applyForLoan()
//        }
//    }
//
//    private fun applyForLoan() {
//        val amountText = etAmount.text.toString().trim()
//        val durationText = etDuration.text.toString().trim()
//        val purpose = etPurpose.text.toString().trim()
//
//        if (amountText.isEmpty() || durationText.isEmpty() || purpose.isEmpty()) {
//            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val amount = amountText.toDoubleOrNull()
//        val duration = durationText.toIntOrNull()
//
//        if (amount == null || duration == null) {
//            Toast.makeText(this, "Invalid amount or duration", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (amount > 7000) {
//            Toast.makeText(this, "Maximum loan amount is 7000", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (duration > 3) {
//            Toast.makeText(this, "Maximum duration is 3 months", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val userId = sharedPref.getString("userId", null)
//        if (userId.isNullOrEmpty()) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val loanRequest = LoanRequest(
//            userId = userId,
//            amount = amount.toInt(),
//            purpose = purpose,
//            durationMonths = duration
//        )
//
//        RetrofitClient.apiService.applyLoan(loanRequest)
//            .enqueue(object : Callback<LoanResponse> {
//                override fun onResponse(
//                    call: Call<LoanResponse>,
//                    response: Response<LoanResponse>
//                ) {
//                    if (response.isSuccessful && response.body() != null) {
//                        Toast.makeText(
//                            this@ApplyLoanActivity,
//                            "Loan applied successfully!",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            this@ApplyLoanActivity,
//                            "Failed: ${response.message()}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<LoanResponse>, t: Throwable) {
//                    Toast.makeText(
//                        this@ApplyLoanActivity,
//                        "Error: ${t.localizedMessage}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            })
//    }
//}
//
////package com.patrice.abellegroup
////
////import android.os.Bundle
////import android.widget.Button
////import android.widget.EditText
////import android.widget.Toast
////import androidx.appcompat.app.AppCompatActivity
////import com.patrice.abellegroup.models.LoanRequest
////import com.patrice.abellegroup.models.LoanResponse
////import com.patrice.abellegroup.api.RetrofitClient
////import retrofit2.Call
////import retrofit2.Callback
////import retrofit2.Response
////
////class ApplyLoanActivity : AppCompatActivity() {
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_apply_loan)
////
////        val etFullName = findViewById<EditText>(R.id.etFullName)
////        val etAmount = findViewById<EditText>(R.id.etAmount)
////        val etPurpose = findViewById<EditText>(R.id.etPurpose)
////        val etDuration = findViewById<EditText>(R.id.etDuration)
////        val btnApply = findViewById<Button>(R.id.btnApply)
////
////        btnApply.setOnClickListener {
////            val name = etFullName.text.toString().trim()  // for now, not sent to API
////            val amount = etAmount.text.toString().trim().toIntOrNull() ?: 0
////            val purpose = etPurpose.text.toString().trim()
////            val duration = etDuration.text.toString().trim().toIntOrNull() ?: 0
////
////            when {
////                name.isEmpty() || amount == 0 || purpose.isEmpty() || duration == 0 -> {
////                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
////                }
////                amount > 7000 -> {
////                    Toast.makeText(this, "Maximum loan amount is 7000", Toast.LENGTH_SHORT).show()
////                }
////                duration > 3 -> {
////                    Toast.makeText(this, "Maximum duration is 3 months", Toast.LENGTH_SHORT).show()
////                }
////                else -> {
////                    // ⚠️ For now, using a placeholder UserId (replace later with logged-in user ID)
////                    val loanRequest = LoanRequest(
////                        userId = "64f2b1f9c1234567890abcd1",
////                        amount = amount,
////                        purpose = purpose,
////                        durationMonths = duration
////                    )
////
////                    RetrofitClient.apiService.applyLoan(LoanRequest)
////                        .enqueue(object : Callback<LoanResponse> {
////                            override fun onResponse(
////                                call: Call<LoanResponse>,
////                                response: Response<LoanResponse>
////                            ) {
////                                if (response.isSuccessful) {
////                                    Toast.makeText(
////                                        this@ApplyLoanActivity,
////                                        "✅ ${response.body()?.message}",
////                                        Toast.LENGTH_LONG
////                                    ).show()
////                                } else {
////                                    Toast.makeText(
////                                        this@ApplyLoanActivity,
////                                        "❌ Failed: ${response.errorBody()?.string()}",
////                                        Toast.LENGTH_LONG
////                                    ).show()
////                                }
////                            }
////
////                            override fun onFailure(call: Call<LoanResponse>, t: Throwable) {
////                                Toast.makeText(
////                                    this@ApplyLoanActivity,
////                                    "⚠️ Error: ${t.message}",
////                                    Toast.LENGTH_LONG
////                                ).show()
////                            }
////                        })
////                }
////            }
////        }
////    }
////}
