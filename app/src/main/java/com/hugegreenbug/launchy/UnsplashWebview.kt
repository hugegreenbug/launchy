package com.hugegreenbug.launchy

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class UnsplashWebview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash_web)

        val extras = intent.extras
        var url:String = extras?.getString(unsplashWebKey) ?: "https://unsplash.com"

        val webView:WebView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()

        // this will load the url of the website
        webView.loadUrl(url)

        // this will enable the javascript settings
        webView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        webView.settings.setSupportZoom(true)
    }

    companion object {
        const val unsplashWebKey:String="unsplashWebUrl"
    }
}