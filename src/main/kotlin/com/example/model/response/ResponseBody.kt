package com.example.model.response

import com.example.model.order.OrderType
import java.math.BigInteger

data class ResponseBody(
    val orderId : String,
    val quantity : BigInteger,
    val type : OrderType,
    val price : BigInteger
)
