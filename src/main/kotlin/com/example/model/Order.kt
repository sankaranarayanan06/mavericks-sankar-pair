package com.example.model

import java.math.BigInteger

class Order(
    var price: BigInteger = BigInteger.ZERO,
    var placedQuantity: BigInteger = BigInteger.ZERO,
    var type: String = "",
    var userName: String = "",
    var esopType: String = "NON_PERFORMANCE"
) {
    var currentQuantity: BigInteger = placedQuantity
    var status: String = "unfilled"
    val orderId: Int = orderIdCounter++

    companion object {
        var orderIdCounter = 1
    }

    fun updateStatus(){
        status = if (this.currentQuantity == BigInteger.ZERO){
            "filled"
        }else{
            "partially filled"
        }
    }

    fun getMinimumQuantity(seller: Order): BigInteger {
        if(currentQuantity < seller.currentQuantity){
            return currentQuantity
        }
        return seller.currentQuantity
    }

    fun checkIfOrderCanBeMatched(buyer:Order, maxBuyerPrice:BigInteger): Boolean {
        if(buyer.status != "filled" && type != buyer.type && price <= buyer.price && buyer.price > maxBuyerPrice){
            return true
        }
        return false
    }

    fun updateExecutedQuantity(orderExecutionQuantity: BigInteger) {
        currentQuantity -= orderExecutionQuantity
    }
}
