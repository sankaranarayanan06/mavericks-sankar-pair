package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import com.example.model.Transaction
import java.math.BigInteger
import kotlin.math.min


fun performBuys(currentOrder: Order, username: String) {
    val n: Int = orderList.size
    while (true) {
        if (currentOrder.currentQuantity == BigInteger.ZERO) break

        var minSellerPrice: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)
        var sellerID = Int.MIN_VALUE


        // Find if seller with PERFORMANCE order fulfils the deal
        for ((orderID, orderPrev) in orderList) {
            if ((orderPrev.esopType == "PERFORMANCE") && (orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                if (orderPrev.price < minSellerPrice) {
                    minSellerPrice = orderPrev.price
                    sellerID = orderID
                }
            }
        }

        // If not found any performance esop seller then go for normal esop seller
        if (sellerID == Int.MIN_VALUE) {
            for ((orderID, orderPrev) in orderList) {
                if ((orderPrev.esopType == "NON_PERFORMANCE") && (orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                    if (orderPrev.price < minSellerPrice) {
                        minSellerPrice = orderPrev.price
                        sellerID = orderID
                    }
                }
            }
        }

        if (sellerID != Int.MIN_VALUE) {
            // performESOPVestings(orderList[sellerID]!!.userName)
            val transQuantity = orderList[sellerID]!!.currentQuantity.min(currentOrder.currentQuantity)

            orderList[sellerID]!!.currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            val orderTotal = minSellerPrice * transQuantity

            val platformCharge =
                if (orderList[sellerID]!!.esopType != "PERFORMANCE") (orderTotal * BigInteger.TWO) / BigInteger.valueOf(
                    100
                ) else BigInteger.ZERO

            addPlatformCharge(platformCharge)

            // Releasing extra amount from lock for partial matching scenario

            WalletHandler.discardLockedAmountFromWallet(username,((currentOrder.price - minSellerPrice) * transQuantity))
            WalletHandler.addFreeAmountInWallet(username,((currentOrder.price - minSellerPrice) * transQuantity))


            // Releasing lock amount worth actual transaction
            WalletHandler.discardLockedAmountFromWallet(username,transQuantity * minSellerPrice)
            WalletHandler.addAmount(sellerID,transQuantity * minSellerPrice - platformCharge)

            // Reducing the esops from seller account
            if (orderList[sellerID]!!.esopType == "PERFORMANCE") {
                inventoryData[orderList[sellerID]!!.userName]!![0].locked -= transQuantity
            } else {
                inventoryData[orderList[sellerID]!!.userName]!![1].locked -= transQuantity
            }

            //Adding ESOP to buyers account
            inventoryData[username]!![1].free += transQuantity

            // Updating buyers transactions
            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions[currentOrder.orderId] = mutableListOf()
            }

            transactions[currentOrder.orderId]!!.add(
                Transaction(
                    transQuantity, minSellerPrice, orderList[sellerID]!!.esopType
                )
            )

            // Updating seller entries
            if (!transactions.containsKey(sellerID)) {
                transactions[sellerID] = mutableListOf()
            }
            transactions[sellerID]!!.add(Transaction(transQuantity, minSellerPrice, orderList[sellerID]!!.esopType))

            currentOrder.status = "partially filled"
            orderList[sellerID]!!.status = "partially filled"

            if (currentOrder.currentQuantity == BigInteger.ZERO) currentOrder.status = "filled"
            if (orderList[sellerID]!!.currentQuantity == BigInteger.ZERO) orderList[sellerID]!!.status = "filled"


        } else {
            break
        }

    }
}
