package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.Order
import com.example.model.SellOrderResponse
import com.example.validations.isValidESOPType
import com.example.validations.order.ifSufficientQuantity


fun addSellOrder(order: Order): MutableMap<String, Any> {
    val username = order.userName
    val result = mutableMapOf<String, Any>()
    val errorList = mutableListOf<String>()

    if (!isValidESOPType(order.esopType)) {
        errorList.add("Invalid ESOP Type")
    }

    if (!ifSufficientQuantity(username, order.currentQuantity, order.esopType)) {
        errorList.add("Insufficient quantity of ESOPs")
    }

    if (errorList.size > 0) {
        result["errors"] = errorList
        return result
    }

    orderList[order.orderId] = order
    transactions[order.orderId] = mutableListOf()

    result["orderDetails"] = SellOrderResponse(order)

    // Locking
    when (order.esopType) {
        "PERFORMANCE" -> {
            inventoryData[order.userName]!![0].free -= order.currentQuantity
            inventoryData[order.userName]!![0].locked += order.currentQuantity

            performSells(order, username)
            return result
        }

        "NON_PERFORMANCE" -> {
            inventoryData[order.userName]!![1].free -= order.currentQuantity
            inventoryData[order.userName]!![1].locked += order.currentQuantity

            performSells(order, username)
            return result
        }

        else -> {
            val error = mutableListOf("Invalid ESOP Type")
            result["errors"] = error
            return result
        }
    }

}
