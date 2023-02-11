package com.example.model.order

import java.math.BigInteger

data class OrderResponse(
    val orderId: Int,
    var quantity: BigInteger,
    var type: OrderType,
    var price: BigInteger
) {
    constructor(order: Order) : this(
        orderId = order.orderId,
        quantity = order.quantity,
        type = order.type,
        price = order.price
    )
}
