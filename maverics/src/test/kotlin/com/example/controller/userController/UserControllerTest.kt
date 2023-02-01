package com.example.controller.userController

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import java.net.http.HttpClient

@MicronautTest
class UserControllerTest {
    @Inject

    @Client("/")
    var client: HttpClient? = null

    fun `valid user test`(){
        val response: MutableList<String> = client.retrieve
    }

}