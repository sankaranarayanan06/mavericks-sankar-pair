package com.example.services.uservalidation

import com.example.services.createUser
import com.example.validations.isEmailValid
import com.example.validations.isUniqueEmail
import com.example.validations.isUniqueUsername
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

@MicronautTest
class EmailTest {
    @Test
    fun `Valid google email`() {
        // Arrange
        val emails: List<String> = listOf(
            "anushkaj@google.com", "anushkaj@sahaj.ai", "dakshin.k1@gmail.com", "dakshin.k1@gmail.ac.in", "meow@cat.io"
        )

        for (email in emails) {
            // Act
            val emailValidation = isEmailValid(email = email)

            // Assertion

            assertEquals(true, emailValidation)
        }

    }


    @Test
    fun `only number in domain is invalid`() {
        // Arrange
        val email = "anushkaj@123.ai"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        assertEquals(false, emailValidation)
    }

    @Test
    fun `domain longer than 255 is invalid`() {
        // Arrange
        val email =
            "anushkaj@a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123a123456789012345678901234567890123456789012345678901234567890123.ai"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        assertFalse(emailValidation)
    }

    @Test
    fun `email with two dots in domain name is invalid`() {
        // Arrange
        val email = "anushkaj@a1234567890..ai"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        assertEquals(false, emailValidation)
    }

    @Test
    fun `local part of email longer than 64 is invalid`() {
        // Arrange
        val email =
            "a1234567890a1234567890a1234567890a1234567890a1234567890a1234567890a1234567890a1234567890@a1234567890..ai"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        assertFalse(emailValidation)
    }

    @Test
    fun `email without @ is invalid`() {
        // Arrange
        val email = "email.com"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        assertFalse(emailValidation)
    }

    @Test
    fun `blank email is invalid`() {
        // Arrange
        val email = ""

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        assertFalse(emailValidation)
    }

    @Test
    fun `email is not already exists in valid`() {
        // Arrange
        val userName = "sat"
        val userName2 = "yam"
        createUser("Satyam", "Baldawa", "8983517226", "email@gmail.com", userName)

        val userNameValidation = isUniqueEmail(userName2)

        Assertions.assertTrue(userNameValidation)
    }

    @Test
    fun `email if already exists in invalid`() {
        createUser("Satyam", "Baldawa", "8983517226", "email@gmail.com", "Sat")

        val userNameValidation = isUniqueEmail("email@gmail.com")

        assertFalse(userNameValidation)
    }
}
