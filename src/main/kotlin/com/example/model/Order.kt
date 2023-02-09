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
        if (this.currentQuantity == BigInteger.ZERO){
            status = "filled"
        }else{
            status = "partially filled"
        }
    }

    fun getMinimumQuantity(seller: Order): BigInteger {
        if(currentQuantity < seller.currentQuantity){
            return currentQuantity
        }
        return seller.currentQuantity
    }
}
