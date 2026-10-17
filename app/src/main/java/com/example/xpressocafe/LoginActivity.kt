package com.example.xpressocafe

import android.content.Intent
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

class LoginActivity : AppCompatActivity() {

    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBarLogin: ProgressBar
    private lateinit var tvLoginStatus: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBarLogin = findViewById(R.id.progressBarLogin)
        tvLoginStatus = findViewById(R.id.tvLoginStatus)

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                tvLoginStatus.text = "Please enter email and password"
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, pass: String) {
        progressBarLogin.visibility = View.VISIBLE
        tvLoginStatus.text = ""
        btnLogin.isEnabled = false

        executor.execute {
            var resultMessage: String
            var isSuccess = false
            var conn: HttpURLConnection? = null

            try {
                val url = URL("http://10.0.2.2/xpresso_api/login.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                val postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(pass, "UTF-8")

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(postData)
                writer.flush()
                writer.close()

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseString)
                    val status = jsonResponse.optString("status")
                    resultMessage = jsonResponse.optString("message")

                    if (status == "success") {
                        isSuccess = true
                    }
                } else {
                    resultMessage = "HTTP Error: ${conn.responseCode}"
                }
            } catch (e: Exception) {
                resultMessage = "Connection Failed: ${e.localizedMessage}"
            } finally {
                conn?.disconnect()
            }

            handler.post {
                progressBarLogin.visibility = View.GONE
                btnLogin.isEnabled = true

                if (isSuccess) {
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to the Menu screen!
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close login screen so the back button doesn't return here
                } else {
                    tvLoginStatus.text = resultMessage
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}