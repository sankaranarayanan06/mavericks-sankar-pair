package com.example.controller

import com.example.constants.allUsers
import com.example.model.User
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class UserControllerTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    fun clearUserData() {
        allUsers.clear()
    }

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
        assertEquals("""{"message":"User sat_yam registered successfully"}""", response)
    }

    @Test
    fun `empty firstname in invalid`() {
        val request = HttpRequest.POST(
            "/user/register", """
            {
                "firstName": "",
                "lastName": "Bal",
                "userName": "sat_yam",
                "email": "amaans@a1234567890a1123456789090a1.ai", 
                "phoneNumber": "9876543210"
            }
        """.trimIndent()
        )

        val exception: HttpClientResponseException = assertThrows {
            client.toBlocking().retrieve(request)
        }

        assertEquals("""{"errors":["firstName cannot be empty"]}""", exception.response.body())
    }


    @Test
    fun `empty lastname and firstname in invalid`() {
        val request = HttpRequest.POST(
            "/user/register", """
            {
                "firstName": "",
                "lastName": "",
                "userName": "sat_yam",
                "email": "amaans@a1234567890a1123456789090a1.ai", 
                "phoneNumber": "9876543210"
            }
        """.trimIndent()
        )

        val exception: HttpClientResponseException = assertThrows {
            client.toBlocking().retrieve(request)
        }

        assertEquals(
            """{"errors":["firstName cannot be empty","lastName cannot be empty"]}""",
            exception.response.body()
        )
    }


    @Test
    fun `empty username in invalid`() {
        val request = HttpRequest.POST(
            "/user/register", """
            {
                "firstName": "sat",
                "lastName": "bal",
                "userName": "",
                "email": "amaans@a1234567890a1123456789090a1.ai", 
                "phoneNumber": "9876543210"
            }
        """.trimIndent()
        )

        val exception: HttpClientResponseException = assertThrows {
            client.toBlocking().retrieve(request)
        }

        assertEquals(
            """{"errors":["userName cannot be empty"]}""",
            exception.response.body()
        )
    }

    @Test
    fun `empty email in invalid`() {
        val request = HttpRequest.POST(
            "/user/register", """
            {
                "firstName": "sat",
                "lastName": "bal",
                "userName": "sat_bal",
                "email": "", 
                "phoneNumber": "9876543210"
            }
        """.trimIndent()
        )

        val exception: HttpClientResponseException = assertThrows {
            client.toBlocking().retrieve(request)
        }

        assertEquals(
            """{"errors":["email cannot be empty"]}""",
            exception.response.body()
        )
    }
}
