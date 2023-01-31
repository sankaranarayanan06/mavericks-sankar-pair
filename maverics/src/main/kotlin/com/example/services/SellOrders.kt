package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.totalPlatformFees
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import com.example.model.Transaction
import java.math.BigInteger
import kotlin.math.min


fun performSells(currentOrder: Order, sellerUser: String) {

    var n = orderList.size
    while (true) {
        if (currentOrder.currentQuantity == 0L) break;
        var maxBuyerPrice: Long = -1;
        var buyerOrderId = -1;
        for ((key,orderPrev) in orderList) {

            if ((orderPrev!!.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev!!.type) && (currentOrder.price <= orderPrev!!.price)) {
                if (orderPrev.price > maxBuyerPrice) {
                    maxBuyerPrice = orderPrev.price
                    buyerOrderId = orderPrev.orderId
                }
            }
        }
        if (buyerOrderId != -1) {
            println(orderList.get(buyerOrderId)!!.orderId.toString() + " " + orderList.get(buyerOrderId)!!.currentQuantity)

            var transQuantity: Long = min(orderList[buyerOrderId]!!.currentQuantity, currentOrder.currentQuantity)
            orderList[buyerOrderId]!!.currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            // Return amount for high buy low sell scenario
            var returnAmount: Long = ((maxBuyerPrice - currentOrder.price) * transQuantity)
            walletList.get(orderList.get(buyerOrderId)!!.userName)!!.lockedAmount -= returnAmount
            walletList.get(orderList.get(buyerOrderId)!!.userName)!!.freeAmount += returnAmount

            // Get seller amount added to seller's account
            // Reduce the locked amount from buyer account
            // Add ESOPs to buyer account
            var orderTotal = transQuantity * currentOrder.price
            var platformCharge = if (orderList[currentOrder.orderId]!!.esopType != "PERFORMANCE") (orderTotal * 2) / 100 else 0

            addPlatformCharge(platformCharge)

            walletList.get(sellerUser)!!.freeAmount += (transQuantity * currentOrder.price - platformCharge)
            walletList.get(orderList.get(buyerOrderId)!!.userName)!!.lockedAmount -= (transQuantity * currentOrder.price)


            inventoryData.get(orderList.get(buyerOrderId)!!.userName)!![1].free += (transQuantity)
            if (currentOrder.esopType == "PERFORMANCE")
                inventoryData.get(sellerUser)!![0].locked -= (transQuantity)
            else
                inventoryData.get(sellerUser)!![1].locked -= (transQuantity)

            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions.put(currentOrder.orderId, mutableListOf<Transaction>());
            }

            transactions.get(currentOrder.orderId)!!.add(Transaction(transQuantity, currentOrder.price, orderList[currentOrder.orderId]!!.esopType))

            if (!transactions.containsKey(orderList.get(buyerOrderId)!!.orderId)) {
                transactions.put(orderList.get(buyerOrderId)!!.orderId, mutableListOf<Transaction>())
            }

            transactions.get(buyerOrderId)!!.add(Transaction(transQuantity, currentOrder.price, orderList[currentOrder.orderId]!!.esopType))

            currentOrder.status = "partially filled"
            orderList[buyerOrderId]!!.status = "partially filled"


            if (currentOrder.currentQuantity == 0L) currentOrder.status = "filled"
            if (orderList[buyerOrderId]!!.currentQuantity == 0L) orderList[buyerOrderId]!!.status = "filled"

        } else break;
    }

    // orderList.set(currentOrder.orderId,currentOrder)

}
