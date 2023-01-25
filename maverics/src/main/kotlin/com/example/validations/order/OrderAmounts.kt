package com.example.validations.order

import com.example.constants.inventorMap
import com.example.constants.inventoryList
import com.example.constants.maxQuantity
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
    } else {
        return inventoryList[1].free >= quantity
    }
}


fun orderValidation(orderError: MutableList<String>, quantity: Long, type: String, price: Long) {
    if (quantity !in 1..maxQuantity) {
        orderError.add("Quantity out of Range. Max: 10 Million, Min: 1")
    }
    if (price !in 1..maxQuantity) {
        orderError.add("Price out of Range. Max: 10 Million, Min: 1")
    }
    if (quantity * price !in 1..maxQuantity) {
        orderError.add("Total Price [Quantity * Price] out of Range. Max: 10 Million, Min: 1")
    }
    if (type != "SELL" && type != "BUY") {
        orderError.add("Wrong order type")
    }
}

fun orderoverflowValidation(orderError: MutableList<String>, username: String, quantity: Long, price: Long, type: String) {
    if (quantity * price + walletList[username]!!.freeAmount + walletList[username]!!.lockedAmount > maxQuantity && type == "SELL"){
        orderError.add("Cant create order. Wallet will overflow")
    }

    var inventorylist = inventorMap[username]!!
    if(quantity + inventorylist[0].free + inventorylist[1].free + inventorylist[0].locked + inventorylist[1].locked > maxQuantity && type == "BUY"){
        orderError.add("Cant create order. Inventory will overflow")
    }
}
