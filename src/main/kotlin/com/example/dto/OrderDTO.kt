package com.example.dto

import com.example.model.inventory.EsopType
import com.example.model.order.OrderType
import java.math.BigInteger

data class OrderDTO(
    var price: BigInteger,
    var quantity: BigInteger,
    var type: OrderType,
    var esopType: EsopType
)

