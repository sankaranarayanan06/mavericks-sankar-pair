package com.example.model

import com.example.model.EsopType.NON_PERFORMANCE
import java.math.BigInteger

enum class EsopType(val type: String) {
    NON_PERFORMANCE("NON_PERFORMANCE"),
    PERFORMANCE("PERFORMANCE")
}

data class Inventory(
    private var free: BigInteger = BigInteger.ZERO,
    private var locked: BigInteger = BigInteger.ZERO,
    private var type: EsopType = NON_PERFORMANCE
) {
    fun getFreeEsop(): BigInteger {
        return free
    }

    fun getLockedEsop(): BigInteger {
        return locked
    }

    fun getType(): EsopType {
        return type
    }
}
