package com.example.model

import com.example.model.OrderStatus.*
import java.math.BigInteger

data class Order(
    var price: BigInteger = BigInteger.ZERO,
    var quantity: BigInteger = BigInteger.ZERO,
    var userName: String = "",
    var type: OrderType,
    var esopType: EsopType
) {
    var remainingQuantity: BigInteger = quantity
    var status = UNFILLED
    val orderId: Int = orderIdCounter++

    companion object {
        var orderIdCounter = 1
    }

    fun updateStatus() {
        status = if (this.remainingQuantity == BigInteger.ZERO) {
            FILLED
        } else {
            PARTIALLY_FILLED
        }
    }

    fun getMinimumQuantity(seller: Order): BigInteger {
        if (remainingQuantity < seller.remainingQuantity) {
            return remainingQuantity
        }
        return seller.remainingQuantity
    }

    fun checkIfOrderCanBeMatched(buyer: Order, maxBuyerPrice: BigInteger): Boolean {
        if (buyer.status != FILLED && type != buyer.type && price <= buyer.price && buyer.price > maxBuyerPrice) {
            return true
        }
        return false
    }

    fun updateExecutedQuantity(orderExecutionQuantity: BigInteger) {
        remainingQuantity -= orderExecutionQuantity
    }
}
