package com.example.controller

import com.example.model.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
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
class BadRequestControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `It should test empty body`(mapper: ObjectMapper) {
        val request = HttpRequest.POST(
            "/user/register", ""
        )

        val expectedResponse = mapper.writeValueAsString(ErrorResponse(listOf("Empty Body: Send a request body")))

        val exception = assertThrows<HttpClientResponseException> { client.toBlocking().retrieve(request) }
        assertEquals(expectedResponse, exception.response.body())

    }


    @Test
    fun `It should test invalid JSON`(mapper: ObjectMapper) {
        val request = HttpRequest.POST(
            "/user/register", "{"
        )

        val expectedResponse =
            mapper.writeValueAsString(ErrorResponse(listOf("Invalid JSON: Send a request body in valid JSON format")))
        val exception = assertThrows<HttpClientResponseException> { client.toBlocking().retrieve(request) }
        assertEquals(expectedResponse, exception.response.body())

    }
}