package com.example.model

import java.math.BigInteger

data class OrderResponse(
    val orderId: Int,
    var quantity: BigInteger,
    var type: String,
    var price: BigInteger
) {
    constructor(order: Order) : this(
        orderId = order.orderId,
        quantity = order.quantity,
        type = order.type,
        price = order.price
    )
}
