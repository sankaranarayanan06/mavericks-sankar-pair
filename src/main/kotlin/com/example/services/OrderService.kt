package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.dto.OrderDTO
import com.example.model.Order
import com.example.model.OrderResponse

class OrderService {

    fun placeBuyOrder(body: OrderDTO,username:String): OrderResponse {
        val order = Order(body.price,body.quantity,body.type,username)
        val orderAmount = order.price * order.placedQuantity

        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        // Locking amount for order placing
        WalletHandler.addLockedAmountInWallet(username, orderAmount)
        WalletHandler.discardedFreeAmountFromWallet(username, orderAmount)

        performBuys(order, username)

        return OrderResponse(order)
    }

    fun placeSellOrder(body: OrderDTO,username:String): OrderResponse {
        val order = Order(body.price,body.quantity,body.type,username)
        if (body.esopType == "PERFORMANCE") {
            order.esopType = "PERFORMANCE"
        }

        mutableListOf<String>()
        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        // Locking
        when (order.esopType) {
            "PERFORMANCE" -> {
                inventoryData[order.userName]!![0].free -= order.placedQuantity
                inventoryData[order.userName]!![0].locked += order.placedQuantity

                performSells(order, username)
            }

            "NON_PERFORMANCE" -> {
                inventoryData[order.userName]!![1].free -= order.placedQuantity
                inventoryData[order.userName]!![1].locked += order.placedQuantity

                performSells(order, username)
            }
        }
        return OrderResponse(order)
    }
}