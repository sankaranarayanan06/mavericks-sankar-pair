package com.example.UserController

import com.example.validations.isEmailValid
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class EmailTest {
    @Test
    fun `Valid google email`(): Unit {
        // Arrange
        val emails: List<String> = listOf(
            "anushkaj@google.com", "anushkaj@sahaj.ai", "dakshin.k1@gmail.com", "dakshin.k1@gmail.ac.in", "meow@cat.io"
        )

        for (email in emails) {
            // Act
            val emailValidation = isEmailValid(email = email)

            // Assertion

            Assertions.assertEquals(true, emailValidation)
        }

    }


    @Test
    fun `Invalid email only number in domain testing`(): Unit {
        // Arrange
        val email: String = "anushkaj@123.ai"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        Assertions.assertEquals(false, emailValidation)
    }

    @Test
    fun `Invalid email 63 characters domain testing`(): Unit {
        // Arrange
        val email: String = "anushkaj@a1234567890.ai"

        // Act
        val emailValidation = isEmailValid(email = email)

        // Assertion
        Assertions.assertEquals(false, emailValidation)
    }
}