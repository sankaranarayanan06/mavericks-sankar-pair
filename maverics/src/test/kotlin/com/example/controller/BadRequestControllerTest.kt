package com.example.controller

import com.example.model.ErrorResponse
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class BadControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `It should test empty body`() {
        val request = HttpRequest.POST(
            "/user/register", ""
        )

        val expectedResponse = ErrorResponse(listOf("Empty Body: Send a request body"))
        val x = assertThrows<HttpClientResponseException> { client.toBlocking().retrieve(request) }
        assertEquals(expectedResponse, x.response.body())

    }


    @Test
    fun `It should test invalid JSON`() {
        val request = HttpRequest.POST(
            "/user/register", "{"
        )

        val expectedResponse = ErrorResponse(listOf("Empty Body: Send a request body")).toString()
        val x = assertThrows<HttpClientResponseException> { client.toBlocking().retrieve(request) }
        assertEquals(expectedResponse, x.response.body())

    }
}