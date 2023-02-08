package com.example.model

import java.math.BigInteger

class Order(
    var price: BigInteger = BigInteger.ZERO,
    var currentQuantity: BigInteger = BigInteger.ZERO,
    var placedQuantity: BigInteger = BigInteger.ZERO,
    var status: String = "unfilled",
    var type: String = "",
    var esopType: String = "NON_PERFORMANCE",
    var userName: String = "",
    val orderId: Int = orderIdCounter++
) {

    companion object {
        var orderIdCounter = 1
    }
}
