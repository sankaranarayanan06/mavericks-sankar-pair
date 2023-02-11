package com.example.model.response

import java.math.BigInteger

data class LimitsResponse(
    var maxOrderPrice: BigInteger,
    var maxOrderQuantity: BigInteger,
    var maxWalletAmount: BigInteger,
    var maxInventoryQuantity: BigInteger
)
