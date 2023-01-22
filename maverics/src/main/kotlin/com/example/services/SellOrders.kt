package com.example.services

import com.example.controller.inventorMap
import com.example.controller.orderList
import com.example.controller.transactions
import com.example.controller.walletList
import com.example.model.Order
import kotlin.math.min


fun performSells(currentOrder: Order, sellerUser: String) {

    var n = orderList.size
    while (true) {
        if (currentOrder.currentQuantity == 0L) break;
        var maxBuyerPrice: Long = -1;
        var buyerOrderId = -1;
        for (orderNumber in 0..n - 1) {
            var orderPrev = orderList[orderNumber]

            if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price <= orderPrev.price)) {
                if (orderPrev.price > maxBuyerPrice) {
                    maxBuyerPrice = orderPrev.price
                    buyerOrderId = orderPrev.orderId
                }
            }
        }
        if (buyerOrderId != -1) {
            println(orderList.get(buyerOrderId).orderId.toString() + " " + orderList.get(buyerOrderId).currentQuantity)

            var transQuantity: Long = min(orderList[buyerOrderId].currentQuantity, currentOrder.currentQuantity)
            orderList[buyerOrderId].currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            // Return amount for high buy low sell scenario
            var returnAmount: Long = ((maxBuyerPrice - currentOrder.price) * transQuantity)
            walletList.get(orderList.get(buyerOrderId).userName)!!.lockedAmount -= returnAmount
            walletList.get(orderList.get(buyerOrderId).userName)!!.freeAmount += returnAmount

            // Get seller amount added to seller's account
            // Reduce the locked amount from buyer account
            // Add ESOPs to buyer account
            var orderTotal = transQuantity * currentOrder.price
            var platformCharge = (orderTotal * 2) / 100



            walletList.get(sellerUser)!!.freeAmount += (transQuantity * currentOrder.price - platformCharge)
            walletList.get(orderList.get(buyerOrderId).userName)!!.lockedAmount -= (transQuantity * currentOrder.price)


            inventorMap.get(orderList.get(buyerOrderId).userName)!![1].free += (transQuantity)
            if (currentOrder.esopType == "PERFORMANCE")
                inventorMap.get(sellerUser)!![0].locked -= (transQuantity)
            else
                inventorMap.get(sellerUser)!![1].locked -= (transQuantity)

            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions.put(currentOrder.orderId, mutableListOf<Pair<Long, Long>>());
            }

            transactions.get(currentOrder.orderId)!!.add(Pair(transQuantity, currentOrder.price))

            if (!transactions.containsKey(orderList.get(buyerOrderId).orderId)) {
                transactions.put(orderList.get(buyerOrderId).orderId, mutableListOf<Pair<Long, Long>>())
            }

            transactions.get(buyerOrderId)!!.add(Pair(transQuantity, currentOrder.price))

            currentOrder.status = "partially filled"
            orderList[buyerOrderId].status = "partially filled"


            if (currentOrder.currentQuantity == 0L) currentOrder.status = "filled"
            if (orderList[buyerOrderId].currentQuantity == 0L) orderList[buyerOrderId].status = "filled"

        } else break;
    }

    // orderList.set(currentOrder.orderId,currentOrder)

}
