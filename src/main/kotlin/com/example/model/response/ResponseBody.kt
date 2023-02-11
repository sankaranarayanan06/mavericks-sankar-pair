package com.example.model.response

import java.math.BigInteger

data class ResponseBody(
    val orderId : String,
    val quantity : BigInteger,
    val type : String,
    val price : BigInteger
)
