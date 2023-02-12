package com.example.validations.order

import com.example.constants.Limits
import com.example.constants.inventoryData
import com.example.controller.walletList
import com.example.model.inventory.Inventory
import com.example.model.order.Order
import com.example.model.order.OrderType
import java.math.BigInteger


fun ifSufficientAmountInWallet(username: String, amount: BigInteger): Boolean {
    if (walletList[username]!!.freeAmount < amount) {
        return false
    }
    return true
}

fun ifSufficientQuantity(username: String, quantity: BigInteger, orderType: String): Boolean {
    val inventoryList: MutableList<Inventory> = inventoryData[username]!!
    if (orderType == "PERFORMANCE") {
        return inventoryList[0].getFreeEsop() >= quantity
    }
    return inventoryList[1].getFreeEsop() >= quantity

}


fun orderValidation(orderError: MutableList<String>, quantity: BigInteger, type: OrderType, price: BigInteger) {
    if (quantity !in BigInteger.ONE..Limits.MAX_ORDER_QUANTITY) {
        orderError.add("Quantity out of Range. Max: ${Limits.MAX_ORDER_QUANTITY}, Min: 1")
    }
    if (price !in BigInteger.ONE..Limits.MAX_ORDER_PRICE) {
        orderError.add("Price out of Range. Max: ${Limits.MAX_ORDER_PRICE}, Min: 1")
    }
    if (quantity * price !in BigInteger.ONE..Limits.MAX_ORDER_PRICE) {
        orderError.add("Total Price [Quantity * Price] out of Range. Max: ${Limits.MAX_ORDER_PRICE}, Min: 1")
    }
    if (type != OrderType.SELL && type != OrderType.BUY) {
        orderError.add("Wrong order type")
    }
}

fun orderoverflowValidation(
    orderError: MutableList<String>,
    username: String,
    quantity: BigInteger,
    price: BigInteger,
    type: OrderType
) {
    if (quantity * price + walletList[username]!!.freeAmount + walletList[username]!!.lockedAmount > Limits.MAX_WALLET_AMOUNT && type == OrderType.SELL) {
        orderError.add("Cant create order. Wallet will overflow")
    }

    val inventorylist = inventoryData[username]!!
    if (quantity + inventorylist[0].getFreeEsop() + inventorylist[1].getFreeEsop() + inventorylist[0].getLockedEsop() + inventorylist[1].getLockedEsop() > Limits.MAX_WALLET_AMOUNT && type == OrderType.BUY) {
        orderError.add("Cant create order. Inventory will overflow")
    }
}

fun validateOrder(
    currentOrder: Order,
    username: String
): MutableList<String> {
    val errorList = mutableListOf<String>()
    orderValidation(
        errorList,
        currentOrder.quantity,
        currentOrder.type,
        currentOrder.price
    )

    orderoverflowValidation(
        errorList,
        username,
        currentOrder.quantity,
        currentOrder.price,
        currentOrder.type
    )

    if(currentOrder.type == OrderType.BUY){
        if (!ifSufficientAmountInWallet(username, currentOrder.quantity * currentOrder.price)) {
            errorList.add("Insufficient amount in wallet")
        }
    }
    return errorList
}

