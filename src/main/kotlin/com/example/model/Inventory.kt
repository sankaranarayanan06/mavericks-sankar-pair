package com.example.model

import com.example.model.EsopType.NON_PERFORMANCE
import java.math.BigInteger

enum class EsopType(val type: String) {
    NON_PERFORMANCE("NON_PERFORMANCE"),
    PERFORMANCE("PERFORMANCE")
}

data class Inventory(
    var free: BigInteger = BigInteger.ZERO,
    var locked: BigInteger = BigInteger.ZERO,
    var type: EsopType = NON_PERFORMANCE
)
