package com.example.controller

import com.example.constants.response
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error

@Controller("/notFound")
class NotFoundController {

    @Error(status = HttpStatus.NOT_FOUND, global = true)
    fun notFound(): HttpResponse<*>? {
        response["errors"] = mutableListOf("Page not found")
        return HttpResponse.ok(response)
    }
}