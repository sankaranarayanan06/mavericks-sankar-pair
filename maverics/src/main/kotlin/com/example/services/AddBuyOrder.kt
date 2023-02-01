package com.example.services

import com.example.model.Order
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.validations.order.ifSufficientAmountInWallet


fun addBuyOrder(order: Order) : MutableMap<String,Any>
{
    val result = HashMap<String, Any>()

    val username = order.userName
    val orderAmount = order.price * order.currentQuantity
    if (!ifSufficientAmountInWallet(username, orderAmount)) {
        val errorList = mutableListOf<String>()
        errorList.add("Insufficient amount in wallet")
        result["errors"] = errorList
        return result
    }

    orderList[order.orderId] = order
    transactions[order.orderId] = mutableListOf()

    // Locking amount for order placing
    WalletHandler.lockingAmountToPlaceOrder(username,order.currentQuantity * order.price)

    performBuys(order,username)

    result["userName"] = order.userName
    result["quantity"] = order.placedQuantity
    result["price"] = order.price
    result["type"] = order.type

    return result

}
