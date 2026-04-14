package com.example.localbrowser

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var statusText: TextView
    private var localServer: LocalWebServer? = null
    private val PORT = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        statusText = findViewById(R.id.status_text)

        setupWebView()
        startLocalServer()
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
        }
        webView.webViewClient = WebViewClient()
    }

    private fun startLocalServer() {
        try {
            localServer = LocalWebServer(PORT)
            localServer?.start()

            val ipAddress = getLocalIpAddress()
            val url = "http://$ipAddress:$PORT"

            statusText.text = "Server running at: $url"
            webView.loadUrl(url)

        } catch (e: Exception) {
            statusText.text = "Error starting server: ${e.message}"
        }
    }

    private fun getLocalIpAddress(): String {
        return try {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = wifiManager.connectionInfo
            val ipAddress = connectionInfo.ipAddress

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    convertIpAddress(ipAddress)
                } else {
                    "127.0.0.1"
                }
            } else {
                convertIpAddress(ipAddress)
            }
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }

    private fun convertIpAddress(ipAddress: Int): String {
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipAddress).array()
        return bytes.joinToString(".") { (it.toInt() and 0xff).toString() }
    }

    override fun onDestroy() {
        super.onDestroy()
        localServer?.stop()
    }
}
