package com.example.model.order

import java.math.BigInteger

data class OrderRequest(
    var type: String,
    var price: BigInteger,
    var quantity: BigInteger,
    var esopType: String = ""
)
