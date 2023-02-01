package com.example.controller.userController

import com.example.constants.allUsers
import com.example.model.User
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
class UserControllerTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `should register valid user`() {
        val request = HttpRequest.POST(
            "/user/register", """
            {
                "firstName": "Satyam",
                "lastName": "Bal",
                "userName": "sat_yam",
                "email": "amaans@a1234567890a1123456789090a1.ai", 
                "phoneNumber": "9876543210"
            }
        """.trimIndent()
        )

        val response = client.toBlocking().retrieve(request)

        val expected = User("Satyam", "Bal", "9876543210", "amaans@a1234567890a1123456789090a1.ai", "sat_yam")

        assertEquals(expected, allUsers["sat_yam"])
        assertEquals(1, allUsers.size)
        assertEquals("""["User added successfully"]""", response)
    }

}