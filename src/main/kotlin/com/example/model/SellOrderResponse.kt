package com.example.model

import java.math.BigInteger

data class SellOrderResponse(
    val orderId: Int,
    var price: BigInteger,
    var quantity: BigInteger,
    var status: String,
    var type: String,
    var esopType: String
) {
    constructor(order: Order) : this(
        price = order.price,
        quantity = order.quantity,
        status = order.status,
        type = order.type,
        orderId = order.orderId,
        esopType = order.esopType
    )
}
