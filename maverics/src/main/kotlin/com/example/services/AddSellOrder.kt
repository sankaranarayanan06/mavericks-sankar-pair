package com.example.services

import com.example.constants.*
import com.example.model.Order
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

    result["userName"] = order.userName
    result["quantity"] = order.placedQuantity
    result["price"] = order.price
    result["type"] = order.type

    // Locking
    if (order.esopType == "PERFORMANCE") {
        inventoryData[order.userName]!![0].free -= order.currentQuantity
        inventoryData[order.userName]!![0].locked += order.currentQuantity

        performSells(order, username)
        return result
    } else if (order.esopType == "NON_PERFORMANCE") {
        inventoryData[order.userName]!![1].free -= order.currentQuantity
        inventoryData[order.userName]!![0].locked += order.currentQuantity

        performSells(order, username)
        return result
    } else {
        val error = mutableListOf("Invalid ESOP Type")
        result["errors"] = error
        return result
    }

}
