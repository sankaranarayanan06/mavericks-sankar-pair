package com.example.dto

import java.math.BigInteger

data class OrderDTO(
    var price: BigInteger,
    var quantity: BigInteger,
    var type: String,
    var esopType: String?
)