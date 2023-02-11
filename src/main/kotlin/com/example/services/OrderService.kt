package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.dto.OrderDTO
import com.example.exception.ErrorResponseBodyException
import com.example.model.Order
import com.example.model.OrderResponse
import com.example.validations.isUserExists
import com.example.validations.order.validateOrder

class OrderService{

    private val performOrder = PerformOrder()
    fun placeBuyOrder(order: Order,username:String): OrderResponse {
        val orderAmount = order.price * order.quantity

        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        // Locking amount for order placing
        WalletHandler.addLockedAmountInWallet(username, orderAmount)
        WalletHandler.discardedFreeAmountFromWallet(username, orderAmount)

        performOrder.performBuys(order, username)

        return OrderResponse(order)
    }

    fun placeSellOrder(order: Order,esopType: String,username:String): OrderResponse {
        if (esopType == "PERFORMANCE") {
            order.esopType = "PERFORMANCE"
        }

        mutableListOf<String>()
        orderList[order.orderId] = order
        transactions[order.orderId] = mutableListOf()

        // Locking
        when (order.esopType) {
            "PERFORMANCE" -> {
                inventoryData[order.userName]!![0].free -= order.quantity
                inventoryData[order.userName]!![0].locked += order.quantity

                performOrder.performSells(order, username)
            }

            "NON_PERFORMANCE" -> {
                inventoryData[order.userName]!![1].free -= order.quantity
                inventoryData[order.userName]!![1].locked += order.quantity

                performOrder.performSells(order, username)
            }
        }
        return OrderResponse(order)
    }

    fun placeOrder(orderRequest: OrderDTO,username: String): OrderResponse{
        if (!isUserExists(username)) {
            throw ErrorResponseBodyException("User does not exist.")
        }

        val currentOrder = Order(orderRequest.price, orderRequest.quantity, orderRequest.type, username)
        val errors = validateOrder(currentOrder, username)
        if (errors.size > 0) {
            throw ErrorResponseBodyException(errors)
        }

        val response: OrderResponse = if (currentOrder.type == "BUY") {
            placeBuyOrder(currentOrder, username)
        } else {
            placeSellOrder(currentOrder, orderRequest.esopType!!, username)
        }
        return response
    }
}