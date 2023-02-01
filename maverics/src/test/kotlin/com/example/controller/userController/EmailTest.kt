package com.example.UserController

import com.example.validations.isEmailValid
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Test
fun `Email testing function`(): Unit {
    // Arrange
    val email: String = "anushka@google.com"

    // Act
    val emailValidation = isEmailValid(email = email)

    // Assertion
    Assertions.assertEquals(true,emailValidation )
}