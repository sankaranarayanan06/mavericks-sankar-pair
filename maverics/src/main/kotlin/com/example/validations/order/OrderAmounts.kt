package com.example.validations.order

import com.example.constants.inventorMap
import com.example.constants.maxOrderQuantity
import com.example.controller.maxWalletAmount
import com.example.controller.walletList
import com.example.model.Inventory


fun ifSufficientAmountInWallet(username: String, amount: Long): Boolean {
    if (walletList[username]!!.freeAmount < amount) {
        return false;
    }
    return true;
}

fun ifSufficientQuantity(username: String, quantity: Long, orderType: String): Boolean {
    var inventoryList: MutableList<Inventory> = inventorMap[username]!!

    if (orderType == "PERFORMANCE") {
        return inventoryList[0].free >= quantity
    }
    return inventoryList[1].free >= quantity
}



fun orderValidation(orderError: MutableList<String>, quantity: Long, type: String, price: Long) {
    if (quantity !in 1..maxOrderQuantity) {
        orderError.add("Quantity out of Range. Max: 100 thousand, Min: 1")
    }
    if (price !in 1..maxWalletAmount) {
        orderError.add("Price out of Range. Max: 100 thousand, Min: 1")
    }
    if (type != "SELL" && type != "BUY") {
        orderError.add("Wrong order type")
    }
}
