package com.patrice.abellegroup

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)

        // Enable JavaScript if your pages need it
        webView.settings.javaScriptEnabled = true

        // Stay inside app
        webView.webViewClient = WebViewClient()

        // Get URL & Title from intent
        val url = intent.getStringExtra("url") ?: "https://yourdomain.com"
        val title = intent.getStringExtra("title") ?: "Page"

        supportActionBar?.title = title

        // Load the page
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
