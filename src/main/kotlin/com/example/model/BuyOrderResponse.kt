package com.example.model

import java.math.BigInteger

data class BuyOrderResponse(
    var price: BigInteger,
    var quantity: BigInteger,
    var status: String,
    var type: String,
    val orderId: Int
) {
    constructor(order: Order) : this(
        price = order.price,
        quantity = order.placedQuantity,
        status = order.status,
        type = order.type,
        orderId = order.orderId
    )
}
