package com.example.xpressocafe

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xpressocafe.api.ApiConfig
import com.example.xpressocafe.models.ApiResponse
import com.example.xpressocafe.network.NetworkHelper
import com.example.xpressocafe.utils.JsonParser
import com.example.xpressocafe.utils.ValidationUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Register Activity handling new user registration.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegName: TextInputEditText
    private lateinit var etRegEmail: TextInputEditText
    private lateinit var etRegPassword: TextInputEditText
    private lateinit var btnRegisterUser: MaterialButton
    private lateinit var progressBarReg: ProgressBar
    private lateinit var tvRegStatus: TextView
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etRegName = findViewById(R.id.etRegName)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        btnRegisterUser = findViewById(R.id.btnRegisterUser)
        progressBarReg = findViewById(R.id.progressBarReg)
        tvRegStatus = findViewById(R.id.tvRegStatus)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        btnRegisterUser.setOnClickListener {
            val name = etRegName.text?.toString()?.trim() ?: ""
            val email = etRegEmail.text?.toString()?.trim() ?: ""
            val password = etRegPassword.text?.toString()?.trim() ?: ""

            val validationError = ValidationUtils.validateRegistrationInput(name, email, password)
            if (validationError != null) {
                tvRegStatus.text = validationError
                tvRegStatus.setTextColor(Color.parseColor("#D32F2F"))
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }

        tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(name: String, email: String, pass: String) {
        progressBarReg.visibility = View.VISIBLE
        tvRegStatus.text = ""
        btnRegisterUser.isEnabled = false

        val params = mapOf(
            "name" to name,
            "email" to email,
            "password" to pass
        )

        NetworkHelper.sendPostRequest(
            urlString = ApiConfig.REGISTER_URL,
            postParams = params,
            callback = object : NetworkHelper.NetworkCallback {
                override fun onSuccess(responseString: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarReg.visibility = View.GONE
                    btnRegisterUser.isEnabled = true

                    val response: ApiResponse<Unit> = JsonParser.parseSimpleResponse(responseString)
                    if (response.isSuccess) {
                        Toast.makeText(this@RegisterActivity, "Account created! Please sign in.", Toast.LENGTH_LONG).show()
                        etRegName.text?.clear()
                        etRegEmail.text?.clear()
                        etRegPassword.text?.clear()
                        tvRegStatus.text = "Account created successfully! You can now log in."
                        tvRegStatus.setTextColor(Color.parseColor("#388E3C"))
                    } else {
                        tvRegStatus.text = response.message.ifEmpty { "Registration failed." }
                        tvRegStatus.setTextColor(Color.parseColor("#D32F2F"))
                    }
                }

                override fun onError(errorMessage: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarReg.visibility = View.GONE
                    btnRegisterUser.isEnabled = true
                    tvRegStatus.text = errorMessage
                    tvRegStatus.setTextColor(Color.parseColor("#D32F2F"))
                }
            }
        )
    }
}
