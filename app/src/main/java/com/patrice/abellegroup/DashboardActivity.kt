package com.patrice.abellegroup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        welcomeText.text = "Welcome to your Dashboard!"
    }
}
