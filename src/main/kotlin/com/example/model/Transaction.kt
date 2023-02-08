package com.example.model

import java.math.BigInteger

class Transaction(
    var quantity: BigInteger = BigInteger.ZERO,
    var price: BigInteger = BigInteger.ZERO,
    var esopType: String
)
