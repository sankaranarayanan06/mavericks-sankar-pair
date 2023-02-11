package com.example.model.wallet

import java.math.BigInteger

data class Wallet(
    var freeAmount: BigInteger = BigInteger.ZERO,
    var lockedAmount: BigInteger = BigInteger.ZERO
) {

}
