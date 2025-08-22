package com.patrice.abellegroup

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.patrice.abellegroup.api.RetrofitClient
import com.patrice.abellegroup.models.SignupResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Calendar

class SignupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var tvSelectedDate: TextView
    private lateinit var imageView: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnSignup: Button
    private lateinit var tvError: TextView
    private var imageUri: Uri? = null
    private var selectedDob: String = ""

    private val REQUEST_IMAGE_PICK = 1000
    private val REQUEST_STORAGE_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPhone = findViewById(R.id.etPhone)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        imageView = findViewById(R.id.ivProfileImage)
        btnChooseImage = findViewById(R.id.btnSelectImage)
        btnSignup = findViewById(R.id.btnSignup)
        tvError = findViewById(R.id.tvError)

        // Date picker
        findViewById<Button>(R.id.btnSelectDate).setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    selectedDob = "$year-${month + 1}-$day"
                    tvSelectedDate.text = selectedDob
                },
                calendar.get(Calendar.YEAR) - 18,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            val maxDateCalendar = Calendar.getInstance()
            maxDateCalendar.add(Calendar.YEAR, -18)
            datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
            datePickerDialog.show()
        }

        btnChooseImage.setOnClickListener {
            if (hasStoragePermission()) {
                openImagePicker()
            } else {
                requestStoragePermission()
            }
        }

        btnSignup.setOnClickListener {
            val username = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val dob = selectedDob

            if (!validateInputs(username, email, password, phone, dob)) return@setOnClickListener

            btnSignup.isEnabled = false
            tvError.text = ""

            submitSignup(username, email, phone, dob, password, imageUri)
        }
    }

    private fun validateInputs(username: String, email: String, password: String, phone: String, dob: String): Boolean {
        when {
            username.isEmpty() -> { etName.error = "Username is required"; return false }
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { etEmail.error = "Valid email is required"; return false }
            phone.isEmpty() -> { etPhone.error = "Phone is required"; return false }
            password.length < 6 -> { etPassword.error = "Password must be at least 6 characters"; return false }
            dob.isEmpty() -> { tvError.text = "Please select your date of birth"; return false }
        }
        return true
    }

//    private fun submitSignup(username: String, email: String, phone: String, dob: String, password: String, imageUri: Uri?) {
//        val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
//        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
//        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
//        val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
//        val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
//
//        var imagePart: MultipartBody.Part? = null
//        if (imageUri != null) {
//            val file = getFileFromUri(imageUri)
//            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//            imagePart = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
//        }
//
//        RetrofitClient.apiService.signup(usernameBody, emailBody, phoneBody, dobBody, passwordBody, imagePart)
//            .enqueue(object : Callback<SignupResponse> {
//                override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
//                    btnSignup.isEnabled = true
//                    if (response.isSuccessful) {
//                        val data = response.body()
//                        try {
//                            val masterKey = MasterKey.Builder(this@SignupActivity)
//                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                                .build()
//                            val prefs = EncryptedSharedPreferences.create(
//                                this@SignupActivity,
//                                "auth_secure",
//                                masterKey,
//                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                            )
//                            prefs.edit()
//                                .putString("token", data?.token)
//                                .putString("role", data?.role)
//                                .apply()
//                        } catch (e: Exception) {
//                            tvError.text = "Secure storage error"
//                            return
//                        }
//                        Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@SignupActivity, DashboardActivity::class.java))
//                        finish()
//                    } else {
//                        tvError.text = "Signup failed: ${response.message()}"
//                    }
//                }
//
//                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
//                    btnSignup.isEnabled = true
//                    tvError.text = "Network error: ${t.message}"
//                }
//            })
//    }
private fun submitSignup(username: String, email: String, phone: String, dob: String, password: String, imageUri: Uri?) {
    val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
    val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
    val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
    val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
    val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

    var imagePart: MultipartBody.Part? = null
    if (imageUri != null) {
        val file = getFileFromUri(imageUri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        imagePart = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
    }

    RetrofitClient.apiService.signup(
        usernameBody,
        emailBody,
        phoneBody,
        dobBody,
        passwordBody,
        imagePart
    ).enqueue(object : Callback<SignupResponse> {
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
                        .putString("token", data?.accessToken)
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

    private fun getFileFromUri(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return tempFile
    }

    private fun hasStoragePermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_STORAGE_PERMISSION)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }
}


//package com.patrice.abellegroup
//
//import android.Manifest
//import android.app.Activity
//import android.app.DatePickerDialog
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKey
//import com.patrice.abellegroup.api.RetrofitClient
//import com.patrice.abellegroup.models.SignupRequest
//import com.patrice.abellegroup.models.SignupResponse
//import com.patrice.abellegroup.models.UploadResponse
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.File
//import java.util.Calendar
//
//class SignupActivity : AppCompatActivity() {
//
//    private lateinit var etName: EditText
//    private lateinit var etEmail: EditText
//    private lateinit var etPassword: EditText
//    private lateinit var etPhone: EditText
//    private lateinit var tvSelectedDate: TextView
//    private lateinit var imageView: ImageView
//    private lateinit var btnChooseImage: Button
//    private lateinit var btnSignup: Button
//    private lateinit var tvError: TextView
//    private var imageUri: Uri? = null
//    private var selectedDob: String = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup)
//
//        etName = findViewById(R.id.etName)
//        etEmail = findViewById(R.id.etEmail)
//        etPassword = findViewById(R.id.etPassword)
//        etPhone = findViewById(R.id.etPhone)
//        tvSelectedDate = findViewById(R.id.tvSelectedDate)
//        imageView = findViewById(R.id.ivProfileImage)
//        btnChooseImage = findViewById(R.id.btnSelectImage)
//        btnSignup = findViewById(R.id.btnSignup)
//        tvError = findViewById(R.id.tvError)
//
//        checkStoragePermission()
//
//        // Date picker dialog (18+ restriction)
//        findViewById<Button>(R.id.btnSelectDate).setOnClickListener {
//            val calendar = Calendar.getInstance()
//            val datePickerDialog = DatePickerDialog(
//                this,
//                { _, year, month, day ->
//                    selectedDob = "$year-${month + 1}-$day"
//                    tvSelectedDate.text = selectedDob
//                },
//                calendar.get(Calendar.YEAR) - 18,
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//            )
//
//            val maxDateCalendar = Calendar.getInstance()
//            maxDateCalendar.add(Calendar.YEAR, -18)
//            datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
//            datePickerDialog.show()
//        }
//
//        btnChooseImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, 1000)
//        }
//
//        btnSignup.setOnClickListener {
//            val username = etName.text.toString().trim()
//            val email = etEmail.text.toString().trim()
//            val password = etPassword.text.toString().trim()
//            val phone = etPhone.text.toString().trim()
//            val dob = selectedDob
//
//            if (!validateInputs(username, email, password, phone, dob)) return@setOnClickListener
//
//            btnSignup.isEnabled = false
//            tvError.text = ""
//
//            if (imageUri != null) {
//                uploadImageThenSignup(username, email, phone, dob, password)
//            } else {
//                submitSignup(username, email, phone, dob, password, "")
//            }
//        }
//    }
//
//    private fun validateInputs(username: String, email: String, password: String, phone: String, dob: String): Boolean {
//        when {
//            username.isEmpty() -> { etName.error = "Username is required"; return false }
//            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { etEmail.error = "Valid email is required"; return false }
//            phone.isEmpty() -> { etPhone.error = "Phone is required"; return false }
//            password.length < 6 -> { etPassword.error = "Password must be at least 6 characters"; return false }
//            dob.isEmpty() -> { tvError.text = "Please select your date of birth"; return false }
//        }
//        return true
//    }
//
//    private fun uploadImageThenSignup(username: String, email: String, phone: String, dob: String, password: String) {
//        val file = File(getRealPathFromURI(imageUri!!))
//        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//        RetrofitClient.apiService.uploadImage(body).enqueue(object : Callback<UploadResponse> {
//            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
//                if (response.isSuccessful && response.body() != null) {
//                    val filename = response.body()!!.filename
//                    val fullImageUrl = "http://YOUR_SERVER_IP:3000/uploads/$filename"
//                    submitSignup(username, email, phone, dob, password, fullImageUrl)
//                } else {
//                    btnSignup.isEnabled = true
//                    tvError.text = "Image upload failed: ${response.message()}"
//                }
//            }
//
//            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                btnSignup.isEnabled = true
//                tvError.text = "Image upload failed: ${t.message}"
//            }
//        })
//    }
//
//    private fun submitSignup(username: String, email: String, phone: String, dob: String, password: String, imageUrl: String) {
//        val request = SignupRequest(username, email, phone, dob, imageUrl, password)
//
//        RetrofitClient.apiService.signup(request).enqueue(object : Callback<SignupResponse> {
//            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
//                btnSignup.isEnabled = true
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    try {
//                        val masterKey = MasterKey.Builder(this@SignupActivity)
//                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                            .build()
//                        val prefs = EncryptedSharedPreferences.create(
//                            this@SignupActivity,
//                            "auth_secure",
//                            masterKey,
//                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                        )
//                        prefs.edit()
//                            .putString("token", data?.token)
//                            .putString("role", data?.role)
//                            .apply()
//                    } catch (e: Exception) {
//                        tvError.text = "Secure storage error"
//                        return
//                    }
//                    Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this@SignupActivity, DashboardActivity::class.java))
//                    finish()
//                } else {
//                    tvError.text = "Signup failed: ${response.message()}"
//                }
//            }
//
//            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
//                btnSignup.isEnabled = true
//                tvError.text = "Network error: ${t.message}"
//            }
//        })
//    }
//
//    private fun getRealPathFromURI(uri: Uri): String {
//        val cursor = contentResolver.query(uri, null, null, null, null)
//        return if (cursor != null && cursor.moveToFirst()) {
//            val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//            val path = cursor.getString(index)
//            cursor.close()
//            path
//        } else {
//            uri.path ?: ""
//        }
//    }
//
//    private fun checkStoragePermission() {
//        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Manifest.permission.READ_MEDIA_IMAGES
//        } else {
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        }
//        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(permission), 1001)
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
//            imageUri = data?.data
//            imageView.setImageURI(imageUri)
//        }
//    }
//}
