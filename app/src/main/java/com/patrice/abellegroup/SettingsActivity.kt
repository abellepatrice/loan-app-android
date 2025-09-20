package com.patrice.abellegroup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var spinnerTheme: Spinner
    private lateinit var switchNotifications: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Apply theme before super.onCreate()
        when (prefs.getString("theme", "system")) {
            "light" -> setTheme(R.style.Theme_AbelleGroup_Light)
            "dark" -> setTheme(R.style.Theme_AbelleGroup_Dark)
            else -> setTheme(R.style.Theme_AbelleGroup) // system default
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvTerms).setOnClickListener {
            Toast.makeText(this, "Terms clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvAboutUs).setOnClickListener {
            Toast.makeText(this, "About Us clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvContactSupport).setOnClickListener {
            Toast.makeText(this, "Contact Support clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvFaq).setOnClickListener {
            Toast.makeText(this, "FAQ clicked", Toast.LENGTH_SHORT).show()
        }
    }

    // --- THEME HANDLING ---
    private fun applyTheme(selected: String) {
        val editor = prefs.edit()
        when (selected) {
            getString(R.string.theme_light) -> editor.putString("theme", "light")
            getString(R.string.theme_dark) -> editor.putString("theme", "dark")
            else -> editor.putString("theme", "system")
        }
        editor.apply()

        recreate() // restart to apply new theme
    }

    private fun getThemeIndex(theme: String?): Int {
        return when (theme) {
            "light" -> 0
            "dark" -> 1
            else -> 2
        }
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

//package com.patrice.abellegroup
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.AppCompatDelegate
//import java.util.*
//
//class SettingsActivity : AppCompatActivity() {
//
//    private lateinit var prefs: SharedPreferences
//    private lateinit var spinnerTheme: Spinner
//    private lateinit var switchNotifications: Switch
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_settings)
//        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
//
//        spinnerTheme = findViewById(R.id.spinnerTheme)
//        switchNotifications = findViewById(R.id.switchNotifications)
//
//        // --- THEME HANDLER ---
//        val theme = prefs.getString("theme", "System Default")
//        spinnerTheme.setSelection(getThemeIndex(theme))
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
////            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
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
//    private fun applyTheme(theme: String?) {
//        when (theme) {
//            getString(R.string.theme_light) -> {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                prefs.edit().putString("theme", getString(R.string.theme_light)).apply()
//            }
//            getString(R.string.theme_dark) -> {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                prefs.edit().putString("theme", getString(R.string.theme_dark)).apply()
//            }
//            else -> {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                prefs.edit().putString("theme", getString(R.string.theme_system_default)).apply()
//            }
//        }
//    }
//
//    private fun getThemeIndex(theme: String?): Int {
//        return when (theme) {
//            getString(R.string.theme_light) -> 0
//            getString(R.string.theme_dark) -> 1
//            else -> 2
//        }
//    }
//
//}

//package com.patrice.abellegroup
//
//import android.content.Intent
//import android.content.SharedPreferences
//import android.net.Uri
//import android.os.Bundle
//import android.widget.AdapterView
//import android.widget.LinearLayout
//import android.widget.Spinner
//import android.widget.Switch
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.core.os.LocaleListCompat
//
//
//class SettingsActivity : AppCompatActivity() {
//
//    private lateinit var prefs: SharedPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_settings)
//
//        prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
//
//
//        // ===== Views =====
//        val btnEditProfile = findViewById<LinearLayout>(R.id.btnEditProfile)
//        val spinnerTheme = findViewById<Spinner>(R.id.spinnerTheme)
//        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)
//        val switchNotifications = findViewById<Switch>(R.id.switchNotifications)
//        val tvPrivacyPolicy = findViewById<TextView>(R.id.tvPrivacyPolicy)
//        val tvTerms = findViewById<TextView>(R.id.tvTerms)
//        val tvAboutUs = findViewById<TextView>(R.id.tvAboutUs)
//        val tvContactSupport = findViewById<TextView>(R.id.tvContactSupport)
//        val tvFaq = findViewById<TextView>(R.id.tvFaq)
//        val tvBuildNumber = findViewById<TextView>(R.id.tvBuildNumber)
//
//
//        // ===== Build Number =====
//        tvBuildNumber.text = "Build Number: 1"
//
//        // ===== Edit Profile =====
//        btnEditProfile.setOnClickListener {
//            startActivity(Intent(this, EditProfileActivity::class.java))
//        }
//
//        // ===== Theme =====
//        val themeOptions = resources.getStringArray(R.array.theme_options)
//        val savedTheme = prefs.getString("theme", "System Default")
//        spinnerTheme.setSelection(themeOptions.indexOf(savedTheme))
//
//        spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
//            ) {
//                val selected = themeOptions[position]
//                prefs.edit().putString("theme", selected).apply()
//                when (selected) {
//                    "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                    "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    "System Default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                }
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//
//        // ===== Language =====
//        val langOptions = resources.getStringArray(R.array.language_options)
//        val savedLang = prefs.getString("language", "English")
//        spinnerLanguage.setSelection(langOptions.indexOf(savedLang))
//
//        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
//            ) {
//                val selectedLang = langOptions[position]
//                prefs.edit().putString("language", selectedLang).apply()
//                setAppLanguage(selectedLang)
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//
//        // ===== Notifications =====
//        switchNotifications.isChecked = prefs.getBoolean("notifications", true)
//        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
//            prefs.edit().putBoolean("notifications", isChecked).apply()
//            Toast.makeText(this, if (isChecked) "Notifications Enabled" else "Notifications Disabled", Toast.LENGTH_SHORT).show()
//        }
//
//        // ===== Legal Links =====
//        tvPrivacyPolicy.setOnClickListener {
//            openWebPage("https://yourapp.com/privacy")
//        }
//        tvTerms.setOnClickListener {
//            openWebPage("https://yourapp.com/terms")
//        }
//
//        // ===== Help & Support =====
//        tvAboutUs.setOnClickListener {
//            openWebPage("https://yourapp.com/about")
//        }
//        tvContactSupport.setOnClickListener {
//            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
//                data = Uri.parse("mailto:support@yourapp.com")
//                putExtra(Intent.EXTRA_SUBJECT, "App Support")
//            }
//            startActivity(emailIntent)
//        }
//        tvFaq.setOnClickListener {
//            openWebPage("https://yourapp.com/faq")
//        }
//    }
//
//    // ===== Utility Functions =====
//    private fun openWebPage(url: String) {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        startActivity(intent)
//    }
//
//    private fun setAppLanguage(language: String) {
//        val localeCode = when (language) {
//            "Kiswahili" -> "sw"
//            "French" -> "fr"
//            else -> "en"
//        }
//        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(localeCode)
//        AppCompatDelegate.setApplicationLocales(appLocale)
//    }
//}
