package com.example.model

import java.math.BigInteger

class Inventory(
    var free: BigInteger = BigInteger.ZERO,
    var locked: BigInteger = BigInteger.ZERO,
    var type: String = "PERFORMANCE"
)
