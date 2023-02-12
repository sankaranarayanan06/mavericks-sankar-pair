package com.example.model.inventory

import com.example.model.inventory.EsopType.NON_PERFORMANCE
import java.math.BigInteger

data class Inventory(
    private var free: BigInteger = BigInteger.ZERO,
    private var locked: BigInteger = BigInteger.ZERO,
    private var type: EsopType = NON_PERFORMANCE
) {
    fun addFreeEsops(amount: BigInteger){
        free += amount
    }

    fun getFreeEsop(): BigInteger {
        return free
    }

    fun getLockedEsop(): BigInteger {
        return locked
    }

    fun getType(): EsopType {
        return type
    }

    fun addLockedEsops(quantity: BigInteger) {
        locked += quantity
    }

    fun removeFreeEsops(quantity: BigInteger) {
        free -= quantity
    }

    fun removeLockedEsops(quantity: BigInteger){
        locked -= quantity
    }
}
