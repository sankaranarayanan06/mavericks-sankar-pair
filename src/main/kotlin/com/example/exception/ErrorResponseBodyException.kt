package com.example.exception

class ErrorResponseBodyException(val errors: List<String>) : Throwable() {

    constructor(error: String) : this(listOf(error)) {}

}
