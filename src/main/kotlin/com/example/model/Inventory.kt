package com.example.model

import com.example.model.EsopType.NON_PERFORMANCE
import java.math.BigInteger

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
