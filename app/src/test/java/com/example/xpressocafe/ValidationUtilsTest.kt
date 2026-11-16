package com.example.xpressocafe

import com.example.xpressocafe.utils.ValidationUtils
import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive Unit Tests for ValidationUtils.
 */
class ValidationUtilsTest {

    @Test
    fun isValidEmail_validEmails_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("john.doe@example.com"))
        assertTrue(ValidationUtils.isValidEmail("user@xpresso.co.za"))
        assertTrue(ValidationUtils.isValidEmail("test_123@domain.org"))
    }

    @Test
    fun isValidEmail_invalidEmails_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("plainaddress"))
        assertFalse(ValidationUtils.isValidEmail("@missingusername.com"))
        assertFalse(ValidationUtils.isValidEmail("user@.com"))
    }

    @Test
    fun isValidPassword_lengthCheck() {
        assertTrue(ValidationUtils.isValidPassword("123456"))
        assertTrue(ValidationUtils.isValidPassword("secretPassword"))
        assertFalse(ValidationUtils.isValidPassword("12345"))
        assertFalse(ValidationUtils.isValidPassword(""))
    }

    @Test
    fun isValidName_lengthCheck() {
        assertTrue(ValidationUtils.isValidName("John Doe"))
        assertTrue(ValidationUtils.isValidName("Al"))
        assertFalse(ValidationUtils.isValidName("A"))
        assertFalse(ValidationUtils.isValidName(""))
    }

    @Test
    fun validateRegistrationInput_checksAllFields() {
        assertNull(ValidationUtils.validateRegistrationInput("Jane Doe", "jane@example.com", "secure123"))

        assertNotNull(ValidationUtils.validateRegistrationInput("", "jane@example.com", "secure123"))
        assertNotNull(ValidationUtils.validateRegistrationInput("Jane Doe", "invalid-email", "secure123"))
        assertNotNull(ValidationUtils.validateRegistrationInput("Jane Doe", "jane@example.com", "123"))
    }

    @Test
    fun validateLoginInput_checksEmailAndPassword() {
        assertNull(ValidationUtils.validateLoginInput("user@example.com", "password123"))

        assertNotNull(ValidationUtils.validateLoginInput("invalid-email", "password123"))
        assertNotNull(ValidationUtils.validateLoginInput("user@example.com", ""))
    }
}
