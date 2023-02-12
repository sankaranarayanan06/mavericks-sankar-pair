package com.example.model.history

import com.example.model.inventory.EsopType
import com.example.model.order.OrderStatus
import com.example.model.order.OrderType
import java.math.BigInteger

data class SellOrderHistory(
    val orderId: Int,
    val price: BigInteger,
    val quantity: BigInteger,
    val type: OrderType,
    val esopType: EsopType,
    val status: OrderStatus,
    val filled: MutableList<Transaction>
)
