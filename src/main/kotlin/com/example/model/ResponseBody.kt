package com.example.model

data class ResponseBody(
    val errors: List<String>?,
    val response: OrderResponse?
)
