package com.example.services.uservalidation

import com.example.services.createUser
import com.example.validations.checkUserName
import com.example.validations.isUniqueUsername
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class UserNameTest {
    @Test
    fun `Valid username`() {
        // Arrange
        val userNames: List<String> = listOf(
            "satyam", "SatBal", "Satttt", "bbc"
        )

        for (userName in userNames) {
            // Act
            val userNameValidation = checkUserName(userName)

            assertFalse(userNameValidation)
        }

    }


    @Test
    fun `username containing @ & slash # is invalid`() {
        // Arrange
        val userNames: List<String> = listOf(
            "sat@yam", "&SatBal", "Sat#", "/"
        )

        for (userName in userNames) {
            // Act
            val userNameValidation = checkUserName(userName)

            assertTrue(userNameValidation)
        }
    }

    @Test
    fun `username already exists in invalid`() {
        // Arrange
        val userName = "sat"
        createUser("Satyam", "Baldawa", "8983517226", "email@gmail.com", userName)

        val userNameValidation = isUniqueUsername(userName)

        assertFalse(userNameValidation)
    }

    @Test
    fun `username if not already exists in valid`() {
        // Arrange
        val userName = "sat"
        val userName2 = "yam"
        createUser("Satyam", "Baldawa", "8983517226", "email@gmail.com", userName)

        val userNameValidation = isUniqueUsername(userName2)

        assertTrue(userNameValidation)
    }
}