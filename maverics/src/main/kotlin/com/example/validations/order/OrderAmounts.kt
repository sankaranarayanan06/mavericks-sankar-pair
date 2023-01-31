package com.example.validations.order

import com.example.constants.Amounts
import com.example.constants.inventoryData
import com.example.controller.walletList
import com.example.model.Inventory


fun ifSufficientAmountInWallet(username: String, amount: Long): Boolean {
    if (walletList[username]!!.freeAmount < amount) {
        return false
    }
    return true
}

fun ifSufficientQuantity(username: String, quantity: Long, orderType: String): Boolean {
    val inventoryList: MutableList<Inventory> = inventoryData[username]!!
    if (orderType == "PERFORMANCE") {
        return inventoryList[0].free >= quantity
    } else {
        return inventoryList[1].free >= quantity
    }
}


fun orderValidation(orderError: MutableList<String>, quantity: Long, type: String, price: Long) {
    if (quantity !in 1..Amounts.MAX_ORDER_QUANTITY) {
        orderError.add("Quantity out of Range. Max: ${Amounts.MAX_ORDER_QUANTITY}, Min: 1")
    }
    if (price !in 1..Amounts.MAX_ORDER_PRICE) {
        orderError.add("Price out of Range. Max: ${Amounts.MAX_ORDER_PRICE}, Min: 1")
    }
    if (quantity * price !in 1..Amounts.MAX_ORDER_PRICE) {
        orderError.add("Total Price [Quantity * Price] out of Range. Max: ${Amounts.MAX_ORDER_PRICE}, Min: 1")
    }
    if (type != "SELL" && type != "BUY") {
        orderError.add("Wrong order type")
    }
}

fun orderoverflowValidation(orderError: MutableList<String>, username: String, quantity: Long, price: Long, type: String) {
    if (quantity * price + walletList[username]!!.freeAmount + walletList[username]!!.lockedAmount > Amounts.MAX_WALLET_AMOUNT && type == "SELL"){
        orderError.add("Cant create order. Wallet will overflow")
    }

    val inventorylist = inventoryData[username]!!
    if(quantity + inventorylist[0].free + inventorylist[1].free + inventorylist[0].locked + inventorylist[1].locked > Amounts.MAX_WALLET_AMOUNT && type == "BUY"){
        orderError.add("Cant create order. Inventory will overflow")
    }
}
