package com.patrice.abellegroup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val settingsPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        when (settingsPref.getString("theme", "system")) {
            "light" -> setTheme(R.style.Theme_AbelleGroup_Light)
            "dark" -> setTheme(R.style.Theme_AbelleGroup_Dark)
            else -> setTheme(R.style.Theme_AbelleGroup_Light)
        }

        super.onCreate(savedInstanceState)

        val sharedPref = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        val nextActivity = if (isLoggedIn) {
            DashboardActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }
}

//package com.patrice.abellegroup
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        // --- Apply theme before super.onCreate ---
//        val settingsPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
//        when (settingsPref.getString("theme", "system")) {
//            "light" -> setTheme(R.style.Theme_AbelleGroup_Light)
//            "dark" -> setTheme(R.style.Theme_AbelleGroup_Dark)
//            else -> setTheme(R.style.Theme_AbelleGroup) // system default
//        }
//
//        super.onCreate(savedInstanceState)
//
//        // --- Secure login prefs ---
//        val sharedPref = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
//
//        // --- Check login state ---
//        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
//
//        val nextActivity = if (isLoggedIn) {
//            DashboardActivity::class.java
//        } else {
//            LoginActivity::class.java
//        }
//
//        startActivity(Intent(this, nextActivity))
//        finish()
//    }
//}

//
//package com.patrice.abellegroup
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val sharedPref = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
//
//        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
//
//        val nextActivity = if (isLoggedIn) {
//            DashboardActivity::class.java
//        } else {
//            LoginActivity::class.java
//        }
//
//        startActivity(Intent(this, nextActivity))
//        finish() // close MainActivity from back stack
//    }
//}
