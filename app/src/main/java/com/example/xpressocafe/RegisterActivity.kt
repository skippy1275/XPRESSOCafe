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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegName: EditText
    private lateinit var etRegEmail: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var btnRegisterUser: Button
    private lateinit var progressBarReg: ProgressBar
    private lateinit var tvRegStatus: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etRegName = findViewById(R.id.etRegName)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        btnRegisterUser = findViewById(R.id.btnRegisterUser)
        progressBarReg = findViewById(R.id.progressBarReg)
        tvRegStatus = findViewById(R.id.tvRegStatus)

        btnRegisterUser.setOnClickListener {
            val name = etRegName.text.toString().trim()
            val email = etRegEmail.text.toString().trim()
            val password = etRegPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                tvRegStatus.text = "Please fill in all fields"
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }
    }

    private fun registerUser(name: String, email: String, pass: String) {
        progressBarReg.visibility = View.VISIBLE
        tvRegStatus.text = ""
        btnRegisterUser.isEnabled = false

        executor.execute {
            var resultMessage: String
            var isSuccess = false
            var conn: HttpURLConnection? = null

            try {
                val url = URL("http://10.0.2.2/xpresso_api/register.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                // Format data for POST request safely
                val postData = "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
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
                progressBarReg.visibility = View.GONE
                btnRegisterUser.isEnabled = true

                if (isSuccess) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_LONG).show()
                    // Clear the fields after successful registration
                    etRegName.text.clear()
                    etRegEmail.text.clear()
                    etRegPassword.text.clear()
                    tvRegStatus.text = "Account created successfully!"
                    tvRegStatus.setTextColor(android.graphics.Color.parseColor("#388E3C")) // Green
                } else {
                    tvRegStatus.text = resultMessage
                    tvRegStatus.setTextColor(android.graphics.Color.parseColor("#D32F2F")) // Red
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}