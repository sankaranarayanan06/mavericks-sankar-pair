package com.example.model.history

import java.math.BigInteger

class Transaction(
    var quantity: BigInteger = BigInteger.ZERO,
    var price: BigInteger = BigInteger.ZERO,
    var esopType: String
)
