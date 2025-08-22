package com.patrice.abellegroup

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.io.File
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchDarkMode: Switch
    private lateinit var switchNotifications: Switch
    private lateinit var btnChangeLanguage: Button
    private lateinit var btnPrivacyPolicy: Button
    private lateinit var btnTerms: Button
    private lateinit var btnHelpSupport: Button
    private lateinit var btnSavePdf: Button
    private lateinit var btnOpenPdf: Button

    private val pdfFileName = "settings_summary.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchDarkMode = findViewById(R.id.switchDarkMode)
        switchNotifications = findViewById(R.id.switchNotifications)
        btnChangeLanguage = findViewById(R.id.btnChangeLanguage)
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy)
        btnTerms = findViewById(R.id.btnTerms)
        btnHelpSupport = findViewById(R.id.btnHelpSupport)
        btnSavePdf = findViewById(R.id.btnSavePdf)
        btnOpenPdf = findViewById(R.id.btnOpenPdf)

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // Load saved preferences
        switchDarkMode.isChecked = sharedPref.getBoolean("DarkMode", false)
        switchNotifications.isChecked = sharedPref.getBoolean("Notifications", true)

        // Dark Mode Toggle
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("DarkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Notifications Toggle
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("Notifications", isChecked).apply()
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Notifications $status", Toast.LENGTH_SHORT).show()
        }

        // Change Language
        btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }

        // Privacy Policy
        btnPrivacyPolicy.setOnClickListener {
            Toast.makeText(this, "Privacy Policy opened", Toast.LENGTH_SHORT).show()
        }

        // Terms & Conditions
        btnTerms.setOnClickListener {
            Toast.makeText(this, "Terms & Conditions opened", Toast.LENGTH_SHORT).show()
        }

        // Help & Support
        btnHelpSupport.setOnClickListener {
            Toast.makeText(this, "Help & Support opened", Toast.LENGTH_SHORT).show()
        }

        // Save PDF
        btnSavePdf.setOnClickListener {
            saveSettingsToPdf()
        }

        // Open PDF
        btnOpenPdf.setOnClickListener {
            openPdfFile()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Swahili", "French")
        val languageCodes = arrayOf("en", "sw", "fr")

        AlertDialog.Builder(this)
            .setTitle("Choose Language")
            .setItems(languages) { _, which ->
                confirmLanguageChange(languageCodes[which])
            }
            .show()
    }

    private fun confirmLanguageChange(languageCode: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Language Change")
            .setMessage("Are you sure you want to change the language?")
            .setPositiveButton("Yes") { _, _ ->
                setLocale(languageCode)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        sharedPref.edit().putString("AppLanguage", languageCode).apply()

        recreate()
    }

    private fun saveSettingsToPdf() {
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val darkMode = if (sharedPref.getBoolean("DarkMode", false)) "Enabled" else "Disabled"
        val notifications = if (sharedPref.getBoolean("Notifications", true)) "Enabled" else "Disabled"
        val language = sharedPref.getString("AppLanguage", "English")

        val settingsSummary = """
            App Settings Summary
            
            Dark Mode: $darkMode
            Notifications: $notifications
            Language: $language
        """.trimIndent()

        val file = File(getExternalFilesDir(null), pdfFileName)
        PdfUtils.createPdf(file, settingsSummary)

        Toast.makeText(this, "Settings saved to PDF", Toast.LENGTH_SHORT).show()
    }

    private fun openPdfFile() {
        val file = File(getExternalFilesDir(null), pdfFileName)
        if (file.exists()) {
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show()
        }
    }
}
