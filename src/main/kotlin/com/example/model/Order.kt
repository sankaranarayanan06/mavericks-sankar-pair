package com.example.model

import java.math.BigInteger

class Order(
    var price: BigInteger = BigInteger.ZERO,
    var placedQuantity: BigInteger = BigInteger.ZERO,
    var type: String = "",
    var userName: String = ""
) {
    var currentQuantity: BigInteger = BigInteger.ZERO
    var status: String = "unfilled"
    var esopType = if(type == "SELL") "NON_PERFORMANCE" else ""
    val orderId: Int = orderIdCounter++

    companion object {
        var orderIdCounter = 1
    }
}
