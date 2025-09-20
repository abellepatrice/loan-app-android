package com.patrice.abellegroup

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.patrice.abellegroup.utils.Constants

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        loadUserDataIntoHeader()
    }

    private fun loadUserDataIntoHeader() {
        val headerView = navigationView.getHeaderView(0)
        val tvUsername: TextView = headerView.findViewById(R.id.tvUsername)
        val ivProfileImage: ImageView = headerView.findViewById(R.id.ivProfileImage)

        try {
            val masterKey = MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                this,
                "LoginPrefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val username = prefs.getString("userUsername", "user")
            val profileUrl = prefs.getString("profileImage", null)

            tvUsername.text = username

            if (!profileUrl.isNullOrEmpty()) {
                val profileUrl = Constants.BASE_URL.replace("/api/", "") + profileUrl
                Glide.with(this)
                    .load(profileUrl)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(ivProfileImage)
            } else {
                ivProfileImage.setImageResource(R.drawable.user)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_loans -> {
                startActivity(Intent(this, MyLoansActivity::class.java))
            }
            R.id.nav_apply_loan -> {
                startActivity(Intent(this, ApplyLoanActivity::class.java))
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { _, _ ->
                        logoutUser()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutUser() {
        try {
            val masterKey = MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                this,
                "LoginPrefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            prefs.edit().clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

//package com.patrice.abellegroup
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.Toast
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKey
//import com.google.android.material.navigation.NavigationView
//
//class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
//
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView: NavigationView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        drawerLayout = findViewById(R.id.drawer_layout)
//        navigationView = findViewById(R.id.nav_view)
//
//        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        navigationView.setNavigationItemSelectedListener(this)
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_my_loans -> {
//                startActivity(Intent(this, MyLoansActivity::class.java))
//            }
//            R.id.nav_apply_loan -> {
//                startActivity(Intent(this, ApplyLoanActivity::class.java))
//            }
////            R.id.nav_repayments -> {
////                startActivity(Intent(this, RepaymentActivity::class.java))
////            }
//            R.id.nav_profile -> {
//                startActivity(Intent(this, ProfileActivity::class.java))
//            }
//            R.id.nav_settings -> {
//                startActivity(Intent(this, SettingsActivity::class.java))
//            }
//            R.id.nav_logout -> {
//                AlertDialog.Builder(this)
//                    .setTitle("Logout")
//                    .setMessage("Are you sure you want to log out?")
//                    .setPositiveButton("Yes") { dialog, _ ->
//                        // Clear secure SharedPreferences
//                        try {
//                            val masterKey = MasterKey.Builder(this)
//                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                                .build()
//
//
//                            val prefs = EncryptedSharedPreferences.create(
//                                this,
//                                "auth_secure",
//                                masterKey,
//                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                            )
//
//                            prefs.edit().clear().apply()
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//
//                        // Redirect to Login
//                        val intent = Intent(this, LoginActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        finish()
//                    }
//                    .setNegativeButton("Cancel") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .show()
//            }
//
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }
//}
