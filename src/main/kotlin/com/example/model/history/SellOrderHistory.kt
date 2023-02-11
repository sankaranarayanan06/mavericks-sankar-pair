package com.example.model.history

import java.math.BigInteger

data class SellOrderHistory(
    val orderId: Int,
    val price: BigInteger,
    val quantity: BigInteger,
    val type: String,
    val esopType: String? = null,
    val status: String = "unfilled",
    val filled: MutableList<Transaction>
)
