package com.example.services

import io.micronaut.http.HttpResponse

fun generateErrorResponse(errorList: MutableList<String>): HttpResponse<*> {
    val response = mutableMapOf<String, MutableList<String>>()
    response["errors"] = errorList
    return HttpResponse.ok(response)
}
