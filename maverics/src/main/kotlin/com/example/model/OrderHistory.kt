package com.example.model

import java.math.BigInteger

data class BuyOrderHistory(
    val orderId: Int,
    val price: BigInteger,
    val quantity: BigInteger,
    val type: String,
    val status: String = "unfilled",
    val filled: MutableList<Transaction>
)

data class SellOrderHistory(
    val orderId: Int,
    val price: BigInteger,
    val quantity: BigInteger,
    val type: String,
    val esopType: String? = null,
    val status: String = "unfilled",
    val filled: MutableList<Transaction>
)
