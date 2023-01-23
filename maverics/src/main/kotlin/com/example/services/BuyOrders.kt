package com.example.services

import com.example.constants.inventorMap
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import kotlin.math.min


fun performBuys(currentOrder: Order, username: String){
    var n: Int = orderList.size
    while (true) {
        if (currentOrder.currentQuantity.toLong() == 0L) break;

        var minSellerPrice: Long = 1000000000000000;
        var sellerID = -1;

        // Find if seller with PEROFMANCE order fulfils the deal
        for (orderNumber in 0..n - 1) {
            var orderPrev = orderList[orderNumber]

            // Order should match with SELL and should not be filled
            if ((orderPrev.esopType == "PERFORMANCE") && (orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                if (orderPrev.price < minSellerPrice) {
                    minSellerPrice = orderPrev.price.toLong()
                    sellerID = orderPrev.orderId
                }
            }
        }

        // If not found any performance esop seller then go for normal esop seller
        if (sellerID == -1) {
            for (orderNumber in 0..n - 1) {
                var orderPrev = orderList[orderNumber]

                // Order should match with SELL and should not be filled
                if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                    if (orderPrev.price < minSellerPrice) {
                        minSellerPrice = orderPrev.price.toLong()
                        sellerID = orderPrev.orderId
                    }
                }
            }
        }

        if (sellerID != -1) {
            var transQuantity = min(orderList[sellerID].currentQuantity, currentOrder.currentQuantity)

            orderList[sellerID].currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            var orderTotal = minSellerPrice * transQuantity

            var platformCharge = (orderTotal * 2) / 100

            if(orderList[sellerID].esopType == "PERFORMANCE"){
                platformCharge = 0
            }

            // Releasing extra amount from lock for partial matching scenario
            walletList.get(username)!!.lockedAmount -= ((currentOrder.price - minSellerPrice) * transQuantity)
            walletList.get(username)!!.freeAmount += ((currentOrder.price - minSellerPrice) * transQuantity)

            // Releasing lock amount worth actual transaction
            walletList.get(username)!!.lockedAmount -= (transQuantity * minSellerPrice)
            walletList.get(orderList.get(sellerID).userName)!!.freeAmount += (transQuantity * minSellerPrice - platformCharge)

            // Reducing the esops from seller account
            if (orderList.get(sellerID).esopType == "PERFORMANCE") {
                inventorMap.get(orderList.get(sellerID).userName)!![0].locked -= (transQuantity)
            } else {
                inventorMap.get(orderList.get(sellerID).userName)!![1].locked -= (transQuantity)
            }

            //Adding ESOP to buyers account
            inventorMap.get(username)!![1].free += (transQuantity)

            // Updating buyers transactions
            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions.put(currentOrder.orderId, mutableListOf<Pair<Long, Long>>())
            }

            transactions.get(currentOrder.orderId)!!.add(Pair(transQuantity, minSellerPrice))

            // Updating seller entries
            if (!transactions.containsKey(sellerID)) {
                transactions.put(sellerID, mutableListOf<Pair<Long, Long>>())
            }
            transactions.get(sellerID)!!.add(Pair(transQuantity, minSellerPrice))

            currentOrder.status = "partially filled"
            orderList[sellerID].status = "partially filled"

            if (currentOrder.currentQuantity == 0L) currentOrder.status = "filled"
            if (orderList[sellerID].currentQuantity == 0L) orderList[sellerID].status = "filled"


        } else {
            break;
        }
    }
}
