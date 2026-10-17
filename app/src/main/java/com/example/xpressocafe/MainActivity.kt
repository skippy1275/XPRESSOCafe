package com.example.xpressocafe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var tvMenuOutput: TextView
    private lateinit var btnFetchMenu: Button
    private lateinit var progressBar: ProgressBar

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMenuOutput = findViewById(R.id.tvMenuOutput)
        btnFetchMenu = findViewById(R.id.btnFetchMenu)
        progressBar = findViewById(R.id.progressBar)

        btnFetchMenu.setOnClickListener {
            fetchMenuData()
        }
    }

    private fun fetchMenuData() {
        progressBar.visibility = View.VISIBLE
        tvMenuOutput.text = "Connecting to XAMPP server..."

        executor.execute {
            var resultText: String
            var conn: HttpURLConnection? = null
            try {
                // 10.0.2.2 routes to localhost on your Windows host from the Android Emulator
                val url = URL("http://10.0.2.2/xpresso_api/get_menu.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseString)
                    val status = jsonResponse.optString("status", "")

                    if (status == "success") {
                        val productsArray = jsonResponse.optJSONArray("data")
                        if (productsArray != null && productsArray.length() > 0) {
                            val builder = StringBuilder()
                            for (i in 0 until productsArray.length()) {
                                val item = productsArray.getJSONObject(i)
                                val name = item.optString("productName", item.optString("name", "Item"))
                                val price = item.optString("price", "0.00")
                                builder.append("${i + 1}. $name - R$price\n\n")
                            }
                            resultText = builder.toString().trimEnd()
                        } else {
                            resultText = "No products found."
                        }
                    } else {
                        val message = jsonResponse.optString("message", "")
                        resultText = "API Error: $message"
                    }
                } else {
                    resultText = "HTTP Error Code: $responseCode"
                }
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: e.message ?: ""
                resultText = "Connection Failed: $errorMsg\n\nNote: Ensure XAMPP is running and 10.0.2.2 is accessible."
            } finally {
                conn?.disconnect()
            }

            handler.post {
                if (!isFinishing && !isDestroyed) {
                    progressBar.visibility = View.GONE
                    tvMenuOutput.text = resultText
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}