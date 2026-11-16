package com.example.xpressocafe.utils

import android.util.Patterns

/**
 * Pure validation utility functions for auth forms and user profile updates.
 * Decoupled from Activity logic for unit testing.
 */
object ValidationUtils {

    /**
     * Checks if the given email string has a valid email format.
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return emailRegex.matches(email)
    }

    /**
     * Checks if password meets minimum length requirement (at least 6 characters).
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Checks if name is non-empty and at least 2 characters long.
     */
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    /**
     * Validates full registration form input.
     * @return Error message if invalid, or null if valid.
     */
    fun validateRegistrationInput(name: String, email: String, password: String): String? {
        if (!isValidName(name)) {
            return "Please enter a valid full name (at least 2 characters)."
        }
        if (!isValidEmail(email)) {
            return "Please enter a valid email address."
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters long."
        }
        return null
    }

    /**
     * Validates login input.
     * @return Error message if invalid, or null if valid.
     */
    fun validateLoginInput(email: String, password: String): String? {
        if (!isValidEmail(email)) {
            return "Please enter a valid email address."
        }
        if (password.isBlank()) {
            return "Please enter your password."
        }
        return null
    }

    /**
     * Validates settings update input.
     * @return Error message if invalid, or null if valid.
     */
    fun validateSettingsUpdateInput(newName: String, newPassword: String): String? {
        if (newName.isBlank() && newPassword.isBlank()) {
            return "Please enter a new name or new password to update."
        }
        if (newName.isNotBlank() && !isValidName(newName)) {
            return "New name must be at least 2 characters."
        }
        if (newPassword.isNotBlank() && !isValidPassword(newPassword)) {
            return "New password must be at least 6 characters."
        }
        return null
    }
}
