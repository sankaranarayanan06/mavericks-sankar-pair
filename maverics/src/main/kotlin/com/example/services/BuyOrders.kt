package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import com.example.model.Transaction
import kotlin.math.min


fun performBuys(currentOrder: Order, username: String){
    val n: Int = orderList.size
    while (true) {
        if (currentOrder.currentQuantity == 0L) break

        var minSellerPrice: Long = 1000000000000000
        var sellerID = -1

        // Find if seller with PEROFMANCE order fulfils the deal
        for (orderNumber in 0 until n) {
            val orderPrev = orderList[orderNumber]

            // Order should match with SELL and should not be filled
            if ((orderPrev.esopType == "PERFORMANCE") && (orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                if (orderPrev.price < minSellerPrice) {
                    minSellerPrice = orderPrev.price
                    sellerID = orderPrev.orderId
                }
            }
        }

        // If not found any performance esop seller then go for normal esop seller
        if (sellerID == -1) {
            for (orderNumber in 0 until n) {
                val orderPrev = orderList[orderNumber]

                // Order should match with SELL and should not be filled
                if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                    if (orderPrev.price < minSellerPrice) {
                        minSellerPrice = orderPrev.price
                        sellerID = orderPrev.orderId
                    }
                }
            }
        }

        if (sellerID != -1) {
            performESOPVestings(orderList[sellerID].userName)
            val transQuantity = min(orderList[sellerID].currentQuantity, currentOrder.currentQuantity)

            orderList[sellerID].currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            val orderTotal = minSellerPrice * transQuantity

            val platformCharge = if (orderList[sellerID].esopType != "PERFORMANCE") (orderTotal * 2) / 100 else 0

            addPlatformCharge(platformCharge)

            // Releasing extra amount from lock for partial matching scenario
            walletList[username]!!.lockedAmount -= ((currentOrder.price - minSellerPrice) * transQuantity)
            walletList[username]!!.freeAmount += ((currentOrder.price - minSellerPrice) * transQuantity)

            // Releasing lock amount worth actual transaction
            walletList[username]!!.lockedAmount -= (transQuantity * minSellerPrice)
            walletList[orderList[sellerID].userName]!!.freeAmount += (transQuantity * minSellerPrice - platformCharge)

            // Reducing the esops from seller account
            if (orderList[sellerID].esopType == "PERFORMANCE") {
                inventoryData[orderList[sellerID].userName]!![0].locked -= (transQuantity)
            } else {
                inventoryData[orderList[sellerID].userName]!![1].locked -= (transQuantity)
            }

            //Adding ESOP to buyers account
            inventoryData[username]!![1].free += (transQuantity)

            // Updating buyers transactions
            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions[currentOrder.orderId] = mutableListOf()
            }

            transactions[currentOrder.orderId]!!.add(Transaction(transQuantity, minSellerPrice, orderList[sellerID].esopType))

            // Updating seller entries
            if (!transactions.containsKey(sellerID)) {
                transactions[sellerID] = mutableListOf()
            }
            transactions[sellerID]!!.add(Transaction(transQuantity, minSellerPrice, orderList[sellerID].esopType))

            currentOrder.status = "partially filled"
            orderList[sellerID].status = "partially filled"

            if (currentOrder.currentQuantity == 0L) currentOrder.status = "filled"
            if (orderList[sellerID].currentQuantity == 0L) orderList[sellerID].status = "filled"


        } else {
            break
        }

    }
}
