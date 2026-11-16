package com.example.xpressocafe.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user authentication session using SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "UserPrefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Saves user login session details.
     */
    fun saveUserSession(userId: Int, name: String, email: String) {
        val editor = prefs.edit()
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_LOGGED_IN_EMAIL, email)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    /**
     * Updates stored user name.
     */
    fun updateUserName(name: String) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, name)
        editor.apply()
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Gets stored User ID (-1 if not logged in).
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    /**
     * Gets stored User Name (empty string if not found).
     */
    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    /**
     * Gets stored logged in Email (empty string if not found).
     */
    fun getUserEmail(): String {
        return prefs.getString(KEY_LOGGED_IN_EMAIL, "") ?: ""
    }

    /**
     * Clears user session on logout.
     */
    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
