package com.example.xpressocafe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class SettingsActivity : AppCompatActivity() {

    private lateinit var etUpdateName: EditText
    private lateinit var etUpdatePassword: EditText
    private lateinit var btnUpdateSettings: Button
    private lateinit var progressBarSettings: ProgressBar
    private lateinit var tvSettingsStatus: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        etUpdateName = findViewById(R.id.etUpdateName)
        etUpdatePassword = findViewById(R.id.etUpdatePassword)
        btnUpdateSettings = findViewById(R.id.btnUpdateSettings)
        progressBarSettings = findViewById(R.id.progressBarSettings)
        tvSettingsStatus = findViewById(R.id.tvSettingsStatus)

        // Retrieve the logged-in user's email session
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userEmail = sharedPref.getString("logged_in_email", "")

        btnUpdateSettings.setOnClickListener {
            val newName = etUpdateName.text.toString().trim()
            val newPassword = etUpdatePassword.text.toString().trim()

            if (newName.isEmpty() && newPassword.isEmpty()) {
                tvSettingsStatus.text = "Please enter a new name or password"
                tvSettingsStatus.setTextColor(android.graphics.Color.RED)
                return@setOnClickListener
            }

            if (userEmail.isNullOrEmpty()) {
                tvSettingsStatus.text = "Session error. Please log out and log back in."
                tvSettingsStatus.setTextColor(android.graphics.Color.RED)
                return@setOnClickListener
            }

            updateDatabase(userEmail, newName, newPassword)
        }
    }

    private fun updateDatabase(email: String, newName: String, newPassword: String) {
        progressBarSettings.visibility = View.VISIBLE
        tvSettingsStatus.text = ""
        btnUpdateSettings.isEnabled = false

        executor.execute {
            var resultMessage = ""
            var isSuccess = false

            try {
                // Ensure your PHP file is named correctly in your local htdocs folder
                val url = URL("http://10.0.2.2/xpresso_api/update_settings.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true

                val postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&new_name=" + URLEncoder.encode(newName, "UTF-8") +
                        "&new_password=" + URLEncoder.encode(newPassword, "UTF-8")

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(postData)
                writer.flush()
                writer.close()

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    isSuccess = jsonResponse.optString("status") == "success"
                    resultMessage = jsonResponse.optString("message")
                } else {
                    resultMessage = "HTTP Error: ${conn.responseCode}"
                }
                conn.disconnect()
            } catch (e: Exception) {
                resultMessage = "Connection Failed: ${e.localizedMessage}"
            }

            handler.post {
                progressBarSettings.visibility = View.GONE
                btnUpdateSettings.isEnabled = true
                if (isSuccess) {
                    Toast.makeText(this@SettingsActivity, "Settings Saved!", Toast.LENGTH_SHORT).show()
                    finish() // Close settings and return to Menu
                } else {
                    tvSettingsStatus.text = resultMessage
                    tvSettingsStatus.setTextColor(android.graphics.Color.RED)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}