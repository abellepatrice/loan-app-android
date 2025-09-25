package com.patrice.abellegroup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var spinnerTheme: Spinner
    private lateinit var switchNotifications: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        spinnerTheme = findViewById(R.id.spinnerTheme)
        switchNotifications = findViewById(R.id.switchNotifications)

        // --- THEME HANDLER ---
        val savedTheme = prefs.getString("theme", "system")
        spinnerTheme.setSelection(getThemeIndex(savedTheme))
        spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                val selected = parent?.getItemAtPosition(position).toString()
                applyTheme(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- NOTIFICATIONS SWITCH ---
        val notifEnabled = prefs.getBoolean("notifications", true)
        switchNotifications.isChecked = notifEnabled
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
        }

        // --- Other Click Handlers ---
        findViewById<LinearLayout>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<TextView>(R.id.tvPrivacyPolicy).setOnClickListener {
            openWebPage("https://justpaste.it/561dv", "Privacy Policy")
        }

        findViewById<TextView>(R.id.tvTerms).setOnClickListener {
            openWebPage("https://justpaste.it/9vo5q", "Terms & Conditions")
        }

        findViewById<TextView>(R.id.tvAboutUs).setOnClickListener {
            openWebPage("https://justpaste.it/8eh10", "About Us")
        }

        findViewById<TextView>(R.id.tvContactSupport).setOnClickListener {
            openWebPage("https://justpaste.it/csd39", "Contact Support")
        }

        findViewById<TextView>(R.id.tvFaq).setOnClickListener {
            openWebPage("https://justpaste.it/a4fev", "FAQ")
        }

        findViewById<TextView>(R.id.tvRateUs).setOnClickListener {
            startActivity(Intent(this, RatingActivity::class.java))
        }
    }

    // --- THEME HANDLING ---
    private fun applyTheme(selected: String) {
        val editor = prefs.edit()
        when (selected) {
            getString(R.string.theme_light) -> {
                editor.putString("theme", "light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            getString(R.string.theme_dark) -> {
                editor.putString("theme", "dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                editor.putString("theme", "light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        editor.apply()
    }

    private fun getThemeIndex(theme: String?): Int {
        return when (theme) {
            "light" -> 0
            "dark" -> 1
            else -> 2
        }
    }

    // --- WEBVIEW NAVIGATION ---
    private fun openWebPage(url: String, title: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("title", title)
        startActivity(intent)
    }
}


//package com.patrice.abellegroup
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//
//class SettingsActivity : AppCompatActivity() {
//
//    private lateinit var prefs: SharedPreferences
//    private lateinit var spinnerTheme: Spinner
//    private lateinit var switchNotifications: Switch
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_settings)
//
//        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
//
//        spinnerTheme = findViewById(R.id.spinnerTheme)
//        switchNotifications = findViewById(R.id.switchNotifications)
//
//        // --- THEME HANDLER ---
//        val savedTheme = prefs.getString("theme", "system")
//        spinnerTheme.setSelection(getThemeIndex(savedTheme))
//        spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
//            ) {
//                val selected = parent?.getItemAtPosition(position).toString()
//                applyTheme(selected)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//
//        // --- NOTIFICATIONS SWITCH ---
//        val notifEnabled = prefs.getBoolean("notifications", true)
//        switchNotifications.isChecked = notifEnabled
//        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
//            prefs.edit().putBoolean("notifications", isChecked).apply()
//        }
//
//        // --- Other Click Handlers ---
//        findViewById<LinearLayout>(R.id.btnEditProfile).setOnClickListener {
//            startActivity(Intent(this, EditProfileActivity::class.java))
//        }
//
//        findViewById<TextView>(R.id.tvPrivacyPolicy).setOnClickListener {
//            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<TextView>(R.id.tvTerms).setOnClickListener {
//            Toast.makeText(this, "Terms clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<TextView>(R.id.tvAboutUs).setOnClickListener {
//            Toast.makeText(this, "About Us clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<TextView>(R.id.tvContactSupport).setOnClickListener {
//            Toast.makeText(this, "Contact Support clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<TextView>(R.id.tvFaq).setOnClickListener {
//            Toast.makeText(this, "FAQ clicked", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // --- THEME HANDLING ---
//    private fun applyTheme(selected: String) {
//        val editor = prefs.edit()
//        when (selected) {
//            getString(R.string.theme_light) -> editor.putString("theme", "light")
//            getString(R.string.theme_dark) -> editor.putString("theme", "dark")
//            else -> editor.putString("theme", "system")
//        }
//        editor.apply()
//
//        recreate() // restart to apply new theme
//    }
//
//    private fun getThemeIndex(theme: String?): Int {
//        return when (theme) {
//            "light" -> 0
//            "dark" -> 1
//            else -> 2
//        }
//    }
//}
