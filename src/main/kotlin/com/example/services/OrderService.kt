package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.Order
import com.example.model.OrderResponse

class OrderService {

    fun placeBuyOrder(order: Order): MutableMap<String, OrderResponse> {
        val result = mutableMapOf<String, OrderResponse>()

        val username = order.userName
        val orderAmount = order.price * order.currentQuantity

        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        // Locking amount for order placing
        WalletHandler.addLockedAmountInWallet(username, orderAmount)
        WalletHandler.discardedFreeAmountFromWallet(username, orderAmount)

        performBuys(order, username)

        result["orderDetails"] = OrderResponse(order)
        print(result)
        return result
    }

    fun placeSellOrder(order: Order): MutableMap<String, OrderResponse> {
        val username = order.userName
        val result = mutableMapOf<String, OrderResponse>()

        mutableListOf<String>()
        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        result["orderDetails"] = OrderResponse(order)

        // Locking
        when (order.esopType) {
            "PERFORMANCE" -> {
                inventoryData[order.userName]!![0].free -= order.currentQuantity
                inventoryData[order.userName]!![0].locked += order.currentQuantity

                performSells(order, username)
            }

            "NON_PERFORMANCE" -> {
                inventoryData[order.userName]!![1].free -= order.currentQuantity
                inventoryData[order.userName]!![1].locked += order.currentQuantity

                performSells(order, username)
            }
        }
        return result
    }
}