package com.example.xpressocafe

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
import com.example.xpressocafe.utils.SessionManager
import com.example.xpressocafe.utils.ValidationUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Settings Activity allowing user profile and password updates.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbarSettings: MaterialToolbar
    private lateinit var tvCurrentName: TextView
    private lateinit var tvCurrentEmail: TextView
    private lateinit var etUpdateName: TextInputEditText
    private lateinit var etUpdatePassword: TextInputEditText
    private lateinit var btnUpdateSettings: MaterialButton
    private lateinit var progressBarSettings: ProgressBar
    private lateinit var tvSettingsStatus: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)

        toolbarSettings = findViewById(R.id.toolbarSettings)
        toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        tvCurrentName = findViewById(R.id.tvCurrentName)
        tvCurrentEmail = findViewById(R.id.tvCurrentEmail)
        etUpdateName = findViewById(R.id.etUpdateName)
        etUpdatePassword = findViewById(R.id.etUpdatePassword)
        btnUpdateSettings = findViewById(R.id.btnUpdateSettings)
        progressBarSettings = findViewById(R.id.progressBarSettings)
        tvSettingsStatus = findViewById(R.id.tvSettingsStatus)

        val userEmail = sessionManager.getUserEmail()
        val userName = sessionManager.getUserName()

        tvCurrentName.text = userName.ifEmpty { "User Profile" }
        tvCurrentEmail.text = userEmail.ifEmpty { "Not logged in" }

        btnUpdateSettings.setOnClickListener {
            val newName = etUpdateName.text?.toString()?.trim() ?: ""
            val newPassword = etUpdatePassword.text?.toString()?.trim() ?: ""

            val validationError = ValidationUtils.validateSettingsUpdateInput(newName, newPassword)
            if (validationError != null) {
                tvSettingsStatus.text = validationError
                return@setOnClickListener
            }

            if (userEmail.isEmpty()) {
                tvSettingsStatus.text = "Session error. Please log in again."
                return@setOnClickListener
            }

            updateDatabase(userEmail, newName, newPassword)
        }
    }

    private fun updateDatabase(email: String, newName: String, newPassword: String) {
        progressBarSettings.visibility = View.VISIBLE
        tvSettingsStatus.text = ""
        btnUpdateSettings.isEnabled = false

        val params = mapOf(
            "email" to email,
            "new_name" to newName,
            "new_password" to newPassword
        )

        NetworkHelper.sendPostRequest(
            urlString = ApiConfig.UPDATE_SETTINGS_URL,
            postParams = params,
            callback = object : NetworkHelper.NetworkCallback {
                override fun onSuccess(responseString: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarSettings.visibility = View.GONE
                    btnUpdateSettings.isEnabled = true

                    val response: ApiResponse<Unit> = JsonParser.parseSimpleResponse(responseString)
                    if (response.isSuccess) {
                        if (newName.isNotEmpty()) {
                            sessionManager.updateUserName(newName)
                        }
                        Toast.makeText(this@SettingsActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        tvSettingsStatus.text = response.message.ifEmpty { "Update failed." }
                    }
                }

                override fun onError(errorMessage: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarSettings.visibility = View.GONE
                    btnUpdateSettings.isEnabled = true
                    tvSettingsStatus.text = errorMessage
                }
            }
        )
    }
}
