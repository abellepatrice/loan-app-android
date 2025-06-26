package com.patrice.abellegroup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.patrice.abellegroup.api.RetrofitClient
import com.patrice.abellegroup.models.SignupRequest
import com.patrice.abellegroup.models.SignupResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SignupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var imageView: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnSignup: Button
    private lateinit var tvError: TextView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPhone = findViewById(R.id.etPhone)
        datePicker = findViewById(R.id.tvSelectedDate)
        imageView = findViewById(R.id.ivProfileImage)
        btnChooseImage = findViewById(R.id.btnSelectImage)
        btnSignup = findViewById(R.id.btnSignup)
        tvError = findViewById(R.id.tvError)

        checkStoragePermission()

        btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }

        btnSignup.setOnClickListener {
            val username = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val dob = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth}"

            if (username.isEmpty()) {
                etName.error = "Username is required"
                return@setOnClickListener
            }
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Valid email is required"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                etPhone.error = "Phone is required"
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            btnSignup.isEnabled = false
            tvError.text = ""

            if (imageUri != null) {
                val file = File(getRealPathFromURI(imageUri!!))
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                RetrofitClient.apiService.uploadImage(body).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        val imageUrl = response.body()?.get("url") ?: ""
                        submitSignup(username, email, phone, dob, password, imageUrl)
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        btnSignup.isEnabled = true
                        tvError.text = "Image upload failed: ${t.message}"
                    }
                })
            } else {
                submitSignup(username, email, phone, dob, password, "")
            }
        }
    }

    private fun submitSignup(username: String, email: String, phone: String, dob: String, password: String, imageUrl: String) {
        val request = SignupRequest(username, email, phone, dob, imageUrl, password)

        RetrofitClient.apiService.signup(request).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                btnSignup.isEnabled = true
                if (response.isSuccessful) {
                    val data = response.body()
                    try {
                        val masterKey = MasterKey.Builder(this@SignupActivity)
                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                            .build()
                        val prefs = EncryptedSharedPreferences.create(
                            this@SignupActivity,
                            "auth_secure",
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        )
                        prefs.edit()
                            .putString("token", data?.token)
                            .putString("role", data?.role)
                            .apply()
                    } catch (e: Exception) {
                        tvError.text = "Secure storage error"
                        return
                    }
                    Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    tvError.text = "Signup failed: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                btnSignup.isEnabled = true
                tvError.text = "Network error: ${t.message}"
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = cursor.getString(index)
            cursor.close()
            path
        } else {
            uri.path ?: ""
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }
}
