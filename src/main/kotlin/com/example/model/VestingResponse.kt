package com.example.model

import java.math.BigInteger
import java.time.format.DateTimeFormatter

class VestingResponse(var quantity: BigInteger, var time: String, var esopType: String) {
    constructor(vesting: VestingData) : this(
        quantity = vesting.quantity,
        time = vesting.time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")),
        esopType = vesting.esopType
    )

}
