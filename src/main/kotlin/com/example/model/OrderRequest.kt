package com.example.model

import java.math.BigInteger

data class OrderRequest(
    var type: String,
    var price: BigInteger,
    var quantity: BigInteger,
    var esopType: String = ""
)
