package com.example.controller

import com.example.model.ErrorResponse
import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error

@Controller("/badRequest")
class BadRequestController {
    @Error(global = true)
    fun invalidJson(request: HttpRequest<*>, e: JsonParseException): Any {
        return if (request.body.isEmpty) {
            HttpResponse.badRequest(ErrorResponse(listOf("Invalid JSON: Please send a request body in proper JSON format")))
        } else {
            HttpResponse.badRequest(ErrorResponse(listOf<String>()))
        }
    }

    @Error(global = true)
    fun emptyBody(request: HttpRequest<*>, e: Exception): HttpResponse<*>{
        return if (request.body.isEmpty) {
            HttpResponse.badRequest(ErrorResponse(listOf("Empty Body: Send a request body")))
        } else {
            HttpResponse.badRequest(ErrorResponse(listOf<String>()))
        }
    }
}