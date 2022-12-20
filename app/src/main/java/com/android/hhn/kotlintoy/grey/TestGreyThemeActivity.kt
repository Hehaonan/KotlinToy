package com.android.hhn.kotlintoy.grey

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.android.hhn.kotlintoy.R

class TestGreyThemeActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GreyThemeManager.useGrayTheme(window)
        setContentView(R.layout.kt_activity_grey_theme)
        val webView = findViewById<WebView>(R.id.grey_webview)
        webView.loadUrl("https://weread.qq.com/")
    }

}
