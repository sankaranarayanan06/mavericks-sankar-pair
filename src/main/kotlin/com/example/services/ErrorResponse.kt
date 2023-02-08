package com.example.services

import io.micronaut.http.HttpResponse

fun generateErrorResponse(errorList: MutableList<String>): HttpResponse<Map<String, MutableList<String>>> {
    return HttpResponse.badRequest(mapOf("errors" to errorList))
}
