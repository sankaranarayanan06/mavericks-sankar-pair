package com.example.model.history

import com.example.model.inventory.EsopType
import java.math.BigInteger

class Transaction(
    var quantity: BigInteger = BigInteger.ZERO,
    var price: BigInteger = BigInteger.ZERO,
    var esopType: EsopType
)
