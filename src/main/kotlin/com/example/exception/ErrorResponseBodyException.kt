package com.example.exception

class ErrorResponseBodyException(val errors: List<String>) : Throwable() {

}
