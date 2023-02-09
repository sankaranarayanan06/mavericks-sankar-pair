package com.example.controller

import com.example.exception.ErrorResponseBodyException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error

@Controller
class ErrorResponseBodyExceptionController {
    @Error(global = true)
    fun errorResponseBody(exception: ErrorResponseBodyException): HttpResponse<List<String>> {
        return HttpResponse.badRequest(exception.errors)
    }
}