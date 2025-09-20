package com.patrice.abellegroup

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.patrice.abellegroup.adapters.LoanAdapter
import com.patrice.abellegroup.api.RetrofitClient
import com.patrice.abellegroup.models.Loan
import com.patrice.abellegroup.models.MyLoansResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyLoansActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loanAdapter: LoanAdapter
    private val loans = mutableListOf<Loan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_loans)

        recyclerView = findViewById(R.id.recyclerviewLoans)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loanAdapter = LoanAdapter(loans)
        recyclerView.adapter = loanAdapter

        fetchMyLoans()
    }

    private fun fetchMyLoans() {
        // Use the same prefs as LoginActivity
        val sharedPrefs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "LoginPrefs",
                masterKeyAlias,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        }

        val token = sharedPrefs.getString("authToken", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "No token found, please login again", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.getMyLoans("Bearer $token")
            .enqueue(object : Callback<MyLoansResponse> {
                override fun onResponse(
                    call: Call<MyLoansResponse>,
                    response: Response<MyLoansResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        loans.clear()
                        loans.addAll(response.body()!!.loans)
                        loanAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@MyLoansActivity,
                            "Failed to load loans",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("MyLoansActivity", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<MyLoansResponse>, t: Throwable) {
                    Toast.makeText(
                        this@MyLoansActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MyLoansActivity", "onFailure: ${t.message}", t)
                }
            })
    }
}

//package com.patrice.abellegroup
//
//import android.content.Context
//import android.os.Build
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//import com.patrice.abellegroup.adapters.LoanAdapter
//import com.patrice.abellegroup.api.RetrofitClient.apiService
//import com.patrice.abellegroup.models.Loan
//import com.patrice.abellegroup.models.MyLoansResponse
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class MyLoansActivity : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: LoanAdapter
//    private val loans = mutableListOf<Loan>()  // keep loans here
//
//
//    private val sharedPref by lazy {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            try {
//                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//                EncryptedSharedPreferences.create(
//                    "LoginPrefs",
//                    masterKeyAlias,
//                    applicationContext,
//                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                )
//            } catch (e: Exception) {
//                applicationContext.deleteSharedPreferences("LoginPrefs")
//                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//                EncryptedSharedPreferences.create(
//                    "LoginPrefs",
//                    masterKeyAlias,
//                    applicationContext,
//                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                )
//            }
//        } else {
//            getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_my_loans)
//
//        recyclerView = findViewById(R.id.recyclerLoans)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        adapter = LoanAdapter(loans)
//        recyclerView.adapter = adapter
//
//        fetchMyLoans()
//    }
//
//    private fun fetchMyLoans() {
//        val token = sharedPref.getString("authToken", null)
//        if (token.isNullOrEmpty()) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        apiService.getMyLoans("Bearer $token").enqueue(object : Callback<MyLoansResponse> {
//            override fun onResponse(
//                call: Call<MyLoansResponse>,
//                response: Response<MyLoansResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val loans = response.body()?.loans ?: emptyList()
//                    adapter = LoanAdapter(loans)
//                    recyclerView.adapter = adapter
//                } else {
//                    Toast.makeText(
//                        this@MyLoansActivity,
//                        "Failed: ${response.message()}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//
//            override fun onFailure(call: Call<MyLoansResponse>, t: Throwable) {
//                Toast.makeText(
//                    this@MyLoansActivity,
//                    "Error: ${t.localizedMessage}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        })
//    }
//}
