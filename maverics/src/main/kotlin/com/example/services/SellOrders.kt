package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import com.example.model.Transaction
import kotlin.math.min


fun performSells(currentOrder: Order, sellerUser: String) {

    while (true) {
        if (currentOrder.currentQuantity == 0L) break
        var maxBuyerPrice: Long = Long.MIN_VALUE
        var buyerOrderId = Int.MIN_VALUE
        for ((_, orderPrev) in orderList) {

            if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price <= orderPrev.price)) {
                if (orderPrev.price > maxBuyerPrice) {
                    maxBuyerPrice = orderPrev.price
                    buyerOrderId = orderPrev.orderId
                }
            }
        }
        if (buyerOrderId != Int.MIN_VALUE) {
            println(orderList[buyerOrderId]!!.orderId.toString() + " " + orderList[buyerOrderId]!!.currentQuantity)

            val transQuantity: Long = min(orderList[buyerOrderId]!!.currentQuantity, currentOrder.currentQuantity)
            orderList[buyerOrderId]!!.currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            // Return amount for high buy low sell scenario
            val returnAmount: Long = ((maxBuyerPrice - currentOrder.price) * transQuantity)
            walletList[orderList[buyerOrderId]!!.userName]!!.lockedAmount -= returnAmount
            walletList[orderList[buyerOrderId]!!.userName]!!.freeAmount += returnAmount

            // Get seller amount added to seller's account
            // Reduce the locked amount from buyer account
            // Add ESOPs to buyer account
            val orderTotal = transQuantity * currentOrder.price
            val platformCharge =
                if (orderList[currentOrder.orderId]!!.esopType != "PERFORMANCE") (orderTotal * 2) / 100 else 0

            addPlatformCharge(platformCharge)

            walletList[sellerUser]!!.freeAmount += (transQuantity * currentOrder.price - platformCharge)
            walletList[orderList[buyerOrderId]!!.userName]!!.lockedAmount -= (transQuantity * currentOrder.price)


            inventoryData[orderList[buyerOrderId]!!.userName]!![1].free += (transQuantity)
            if (currentOrder.esopType == "PERFORMANCE")
                inventoryData[sellerUser]!![0].locked -= (transQuantity)
            else
                inventoryData[sellerUser]!![1].locked -= (transQuantity)

            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions[currentOrder.orderId] = mutableListOf()
            }

            transactions[currentOrder.orderId]!!
                .add(Transaction(transQuantity, currentOrder.price, orderList[currentOrder.orderId]!!.esopType))

            if (!transactions.containsKey(orderList[buyerOrderId]!!.orderId)) {
                transactions[orderList[buyerOrderId]!!.orderId] = mutableListOf()
            }

            transactions[buyerOrderId]!!
                .add(Transaction(transQuantity, currentOrder.price, orderList[currentOrder.orderId]!!.esopType))

            currentOrder.status = "partially filled"
            orderList[buyerOrderId]!!.status = "partially filled"


            if (currentOrder.currentQuantity == 0L) currentOrder.status = "filled"
            if (orderList[buyerOrderId]!!.currentQuantity == 0L) orderList[buyerOrderId]!!.status = "filled"

        } else break
    }
}
