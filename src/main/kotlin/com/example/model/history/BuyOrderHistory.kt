package com.example.model.history

import java.math.BigInteger

data class BuyOrderHistory(
    val orderId: Int,
    val price: BigInteger,
    val quantity: BigInteger,
    val type: String,
    val status: String = "unfilled",
    val filled: MutableList<Transaction>
)
