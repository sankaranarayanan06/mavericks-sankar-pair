package com.example.controller

import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error

@Controller("/badRequest")
class BadRequestController {
    @Error(global = true)
    fun invalidJson(request: HttpRequest<*>, e: JsonParseException): Any {
        val response = mutableMapOf<String, MutableList<String>>()
        response["errors"] = mutableListOf("Invalid JSON: Please send a request body body in proper JSON format")
        return HttpResponse.badRequest(response)
    }

    @Error(global = true)
    fun emptyBody(request: HttpRequest<*>, e: Exception): HttpResponse<*>{
        val response = mutableMapOf<String, MutableList<String>>()
        if (request.body.isEmpty) {
            response["errors"] = mutableListOf("EMPTY BODY: Please send a request body")
        }
        return HttpResponse.badRequest(response)
    }
}