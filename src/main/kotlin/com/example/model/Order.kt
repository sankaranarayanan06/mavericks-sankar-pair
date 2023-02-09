package com.example.model

import java.math.BigInteger

class Order(
    var price: BigInteger = BigInteger.ZERO,
    var placedQuantity: BigInteger = BigInteger.ZERO,
    var type: String = "",
    var userName: String = "",
    var esopType: String = "NON_PERFORMANCE"
) {
    var currentQuantity: BigInteger = placedQuantity
    var status: String = "unfilled"
    val orderId: Int = orderIdCounter++

    companion object {
        var orderIdCounter = 1
    }
}
