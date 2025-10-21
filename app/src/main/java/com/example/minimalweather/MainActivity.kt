package com.example.minimalweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var geolocationOrigin: String? = null
    private var geolocationCallback: GeolocationPermissions.Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onGeolocationPermissionsShowPrompt(
                    origin: String,
                    callback: GeolocationPermissions.Callback
                ) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        geolocationOrigin = origin
                        geolocationCallback = callback
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        callback.invoke(origin, true, false)
                    }
                }
            }
            loadUrl("file:///android_asset/weather.html")
        }
        setContentView(webView)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geolocationCallback?.invoke(geolocationOrigin, true, false)
            } else {
                geolocationCallback?.invoke(geolocationOrigin, false, false)
            }
            geolocationOrigin = null
            geolocationCallback = null
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}