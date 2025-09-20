package com.patrice.abellegroup

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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

    private val sharedPref by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                EncryptedSharedPreferences.create(
                    "LoginPrefs",
                    masterKeyAlias,
                    applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                applicationContext.deleteSharedPreferences("LoginPrefs")
                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                EncryptedSharedPreferences.create(
                    "LoginPrefs",
                    masterKeyAlias,
                    applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
        } else {
            getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_loan)

        etAmount = findViewById(R.id.etAmount)
        etPurpose = findViewById(R.id.etPurpose)
        etDuration = findViewById(R.id.etDuration)
        btnApplyLoan = findViewById(R.id.btnApplyLoan)

        btnApplyLoan.setOnClickListener { applyForLoan() }
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

        if (amount > 7000) {
            Toast.makeText(this, "Maximum loan amount is 7000", Toast.LENGTH_SHORT).show()
            return
        }
        if (durationMonths > 3) {
            Toast.makeText(this, "Maximum duration is 3 months", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sharedPref.getString("authToken", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val loanRequest = LoanRequest(
            amount = amount,
            purpose = purpose,
            durationMonths = durationMonths
        )

        apiService.applyLoan("Bearer $token", loanRequest)
            .enqueue(object : Callback<LoanResponse> {
                override fun onResponse(call: Call<LoanResponse>, response: Response<LoanResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ApplyLoanActivity,
                            response.body()?.message ?: "Loan application successful",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ApplyLoanActivity,
                            "Failed: ${response.message()}",
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
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.patrice.abellegroup.R
//import com.patrice.abellegroup.api.RetrofitClient.apiService
//import com.patrice.abellegroup.models.LoanRequest
//import com.patrice.abellegroup.models.LoanResponse
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class ApplyLoanActivity : AppCompatActivity() {
//
//    private lateinit var etAmount: EditText
//    private lateinit var etPurpose: EditText
//    private lateinit var etDuration: EditText
//    private lateinit var btnApplyLoan: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_apply_loan)
//
//        etAmount = findViewById(R.id.etAmount)
//        etPurpose = findViewById(R.id.etPurpose)
//        etDuration = findViewById(R.id.etDuration)
//        btnApplyLoan = findViewById(R.id.btnApplyLoan)
//
//        btnApplyLoan.setOnClickListener { applyForLoan() }
//    }
//
//    private fun applyForLoan() {
//        val amountStr = etAmount.text.toString().trim()
//        val purpose = etPurpose.text.toString().trim()
//        val durationStr = etDuration.text.toString().trim()
//
//        if (amountStr.isEmpty() || purpose.isEmpty() || durationStr.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val amount = amountStr.toIntOrNull()
//        val durationMonths = durationStr.toIntOrNull()
//
//        if (amount == null || durationMonths == null) {
//            Toast.makeText(this, "Invalid number entered", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (amount > 7000) {
//            Toast.makeText(this, "Maximum loan amount is 7000", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (durationMonths > 3) {
//            Toast.makeText(this, "Maximum duration is 3 months", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
//        val token = sharedPref.getString("authToken", null)
//
//        if (token.isNullOrEmpty()) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val loanRequest = LoanRequest(
//            amount = amount,
//            purpose = purpose,
//            durationMonths = durationMonths
//        )
//
//        apiService.applyLoan("Bearer $token", loanRequest)
//            .enqueue(object : Callback<LoanResponse> {
//                override fun onResponse(call: Call<LoanResponse>, response: Response<LoanResponse>) {
//                    if (response.isSuccessful) {
//                        Toast.makeText(
//                            this@ApplyLoanActivity,
//                            response.body()?.message ?: "Loan application successful",
//                            Toast.LENGTH_LONG
//                        ).show()
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
