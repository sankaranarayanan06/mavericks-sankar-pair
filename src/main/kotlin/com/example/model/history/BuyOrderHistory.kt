package com.example.model.history

import com.example.model.order.OrderStatus
import com.example.model.order.OrderType
import java.math.BigInteger

data class BuyOrderHistory(
    val orderId: Int,
    val price: BigInteger,
    val quantity: BigInteger,
    val type: OrderType,
    val status: OrderStatus,
    val filled: MutableList<Transaction>
)
