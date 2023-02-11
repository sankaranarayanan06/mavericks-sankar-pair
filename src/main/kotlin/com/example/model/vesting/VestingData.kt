package com.example.model.vesting

import java.math.BigInteger
import java.time.LocalDateTime

data class VestingData(
    var quantity: BigInteger,
    var time: LocalDateTime,
    var esopType: String
) {

}
