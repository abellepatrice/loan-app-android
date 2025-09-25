package com.patrice.abellegroup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bumptech.glide.Glide
import com.patrice.abellegroup.api.RetrofitClient
import com.patrice.abellegroup.models.UserProfileResponse
import com.patrice.abellegroup.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvDob: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnEditProfile: Button
    private lateinit var ivProfilePic: ImageView

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
        setContentView(R.layout.activity_profile)

        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvDob = findViewById(R.id.tvDob)
        btnLogout = findViewById(R.id.btnLogout)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        ivProfilePic = findViewById(R.id.ivProfilePic)

        val token = sharedPref.getString("authToken", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "Session expired, please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        RetrofitClient.apiService.getUserProfile("Bearer $token")
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val profile = response.body()!!

                        tvUsername.text = profile.username
                        tvEmail.text = profile.email
                        tvPhone.text = profile.phone
                        tvDob.text = profile.dob

                        if (!profile.profileImage.isNullOrEmpty()) {
                            val imageUrl = Constants.BASE_URL.replace("/api/", "") + profile.profileImage
                            Glide.with(this@ProfileActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .circleCrop()
                                .into(ivProfilePic)
                        } else {
                            ivProfilePic.setImageResource(R.drawable.user)
                        }
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Failed to load profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        btnLogout.setOnClickListener {
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }
}


//package com.patrice.abellegroup
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKey
//import androidx.security.crypto.MasterKey.Builder
//import androidx.security.crypto.MasterKeys
//import com.bumptech.glide.Glide
//import com.patrice.abellegroup.api.RetrofitClient
//import com.patrice.abellegroup.models.UserProfileResponse
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class ProfileActivity : AppCompatActivity() {
//
//    private lateinit var tvUsername: TextView
//    private lateinit var tvEmail: TextView
//    private lateinit var tvPhone: TextView
//    private lateinit var tvDob: TextView
//    private lateinit var btnLogout: Button
//    private lateinit var btnEditProfile: Button
//    private lateinit var ivProfilePic: ImageView
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
//        setContentView(R.layout.activity_profile)
//
//        tvUsername = findViewById(R.id.tvUsername)
//        tvEmail = findViewById(R.id.tvEmail)
//        tvPhone = findViewById(R.id.tvPhone)
//        tvDob = findViewById(R.id.tvDob)
//        btnLogout = findViewById(R.id.btnLogout)
//        btnEditProfile = findViewById(R.id.btnEditProfile)
//        ivProfilePic = findViewById(R.id.ivProfilePic)
//
//        val token = sharedPref.getString("authToken", "") ?: ""
//
//        if (token.isEmpty()) {
//            Toast.makeText(this, "Session expired, please log in again.", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }
//
//        RetrofitClient.apiService.getUserProfile("Bearer $token")
//            .enqueue(object : Callback<UserProfileResponse> {
//                override fun onResponse(
//                    call: Call<UserProfileResponse>,
//                    response: Response<UserProfileResponse>
//                ) {
//                    if (response.isSuccessful && response.body() != null) {
//                        val profile = response.body()!!
//
//                        tvUsername.text = profile.username
//                        tvEmail.text = profile.email
//                        tvPhone.text = profile.phone
//                        tvDob.text = profile.dob
//
//                        if (!profile.profileImage.isNullOrEmpty()) {
//                            Glide.with(this@ProfileActivity)
//                                .load(profile.profileImage)
//                                .into(ivProfilePic)
//                        } else {
//                            Glide.with(this@ProfileActivity)
//                                .load(R.drawable.user)
//                                .into(ivProfilePic)
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@ProfileActivity,
//                            "Failed to load profile",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
//                    Toast.makeText(
//                        this@ProfileActivity,
//                        "Network error: ${t.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//
//        // 🔑 3. Logout
//        btnLogout.setOnClickListener {
//            with(sharedPref.edit()) {
//                clear()
//                apply()
//            }
//            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
//
//        // 🔑 4. Edit Profile
//        btnEditProfile.setOnClickListener {
//            startActivity(Intent(this, EditProfileActivity::class.java))
//        }
//    }
//}
//
