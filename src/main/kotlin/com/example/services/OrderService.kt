package com.example.services

import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.BuyOrderResponse
import com.example.model.Order

class OrderService {
    fun placeBuyOrder(order: Order): MutableMap<String, BuyOrderResponse> {
        val result = mutableMapOf<String, BuyOrderResponse>()

        val username = order.userName
        val orderAmount = order.price * order.currentQuantity

        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        // Locking amount for order placing
        WalletHandler.addLockedAmountInWallet(username, orderAmount)
        WalletHandler.discardedFreeAmountFromWallet(username, orderAmount)

        performBuys(order, username)

        result["orderDetails"] = BuyOrderResponse(order)
        print(result)
        return result

    }
}