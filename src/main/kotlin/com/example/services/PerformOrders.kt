package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import com.example.model.Transaction
import java.math.BigInteger

fun performSells(currentOrder: Order, sellerUser: String) {
    while (true) {
        if (currentOrder.currentQuantity == BigInteger.ZERO) break
        var maxBuyerPrice: BigInteger = BigInteger.valueOf(Long.MIN_VALUE)
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

            val transQuantity: BigInteger = orderList[buyerOrderId]!!.currentQuantity.min(currentOrder.currentQuantity)
            orderList[buyerOrderId]!!.currentQuantity -= transQuantity
            currentOrder.currentQuantity -= transQuantity

            // Return amount for high buy low sell scenario
            val returnAmount: BigInteger = ((maxBuyerPrice - currentOrder.price) * transQuantity)
            walletList[orderList[buyerOrderId]!!.userName]!!.lockedAmount -= returnAmount
            walletList[orderList[buyerOrderId]!!.userName]!!.freeAmount += returnAmount

            // Get seller amount added to seller's account
            // Reduce the locked amount from buyer account
            // Add ESOPs to buyer account
            val orderTotal = transQuantity * currentOrder.price
            val platformCharge =
                if (orderList[currentOrder.orderId]!!.esopType != "PERFORMANCE") (orderTotal * BigInteger.TWO) / BigInteger.valueOf(
                    100
                ) else BigInteger.ZERO

            addPlatformCharge(platformCharge)

            walletList[sellerUser]!!.freeAmount += (transQuantity * currentOrder.price - platformCharge)
            walletList[orderList[buyerOrderId]!!.userName]!!.lockedAmount -= (transQuantity * currentOrder.price)


            inventoryData[orderList[buyerOrderId]!!.userName]!![1].free += transQuantity
            if (currentOrder.esopType == "PERFORMANCE")
                inventoryData[sellerUser]!![0].locked -= transQuantity
            else
                inventoryData[sellerUser]!![1].locked -= transQuantity

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


            if (currentOrder.currentQuantity == BigInteger.ZERO) currentOrder.status = "filled"
            if (orderList[buyerOrderId]!!.currentQuantity == BigInteger.ZERO) orderList[buyerOrderId]!!.status =
                "filled"

        } else break
    }
}

fun performBuys(currentOrder: Order, username: String) {
    while (true) {
        if (currentOrder.currentQuantity == BigInteger.ZERO) break

        var minSellerPrice: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)
        var sellerID = Int.MIN_VALUE


        // Find if seller with PERFORMANCE order fulfils the deal
        for ((orderID, orderPrev) in orderList) {
            if ((orderPrev.esopType == "PERFORMANCE")){
                val pair = findSeller(orderPrev, currentOrder, minSellerPrice, sellerID, orderID)
                minSellerPrice = pair.first
                sellerID = pair.second
            }
        }

        // If not found any performance esop seller then go for normal esop seller
        if (sellerID == Int.MIN_VALUE) {
            for ((orderID, orderPrev) in orderList) {
                if (orderPrev.esopType == "NON_PERFORMANCE") {
                    val pair = findSeller(orderPrev, currentOrder, minSellerPrice, sellerID, orderID)
                    minSellerPrice = pair.first
                    sellerID = pair.second
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

            updateBuyOrderDetails(username, currentOrder, minSellerPrice, transQuantity, sellerID, platformCharge)

            // Update the Order History
            updateTransactionDetails(currentOrder.orderId,transQuantity,minSellerPrice)
            updateTransactionDetails(sellerID,transQuantity,minSellerPrice)



            currentOrder.status = "partially filled"
            orderList[sellerID]!!.status = "partially filled"

            if (currentOrder.currentQuantity == BigInteger.ZERO) currentOrder.status = "filled"
            if (orderList[sellerID]!!.currentQuantity == BigInteger.ZERO) orderList[sellerID]!!.status = "filled"


        } else {
            break
        }

    }
}

fun updateTransactionDetails(orderID: Int,transQuantity: BigInteger,minSellerPrice: BigInteger){
    if (!transactions.containsKey(orderID)) {
        transactions[orderID] = mutableListOf()
    }
    transactions[orderID]!!.add(Transaction(transQuantity, minSellerPrice, orderList[orderID]!!.esopType))

}
private fun updateBuyOrderDetails(
    username: String,
    currentOrder: Order,
    minSellerPrice: BigInteger,
    transQuantity: BigInteger,
    sellerID: Int,
    platformCharge: BigInteger
) {
    // Releasing extra amount from lock for partial matching scenario
    WalletHandler.discardLockedAmountFromWallet(
        username,
        ((currentOrder.price - minSellerPrice) * transQuantity)
    )
    WalletHandler.addFreeAmountInWallet(username, ((currentOrder.price - minSellerPrice) * transQuantity))

    // Releasing lock amount worth actual transaction
    WalletHandler.discardLockedAmountFromWallet(username, transQuantity * minSellerPrice)
    WalletHandler.addAmount(sellerID, transQuantity * minSellerPrice - platformCharge)

    // Reducing the esops from seller account
    if (orderList[sellerID]!!.esopType == "PERFORMANCE") {
        inventoryData[orderList[sellerID]!!.userName]!![0].locked -= transQuantity
    } else {
        inventoryData[orderList[sellerID]!!.userName]!![1].locked -= transQuantity
    }

    //Adding ESOP to buyers account
    inventoryData[username]!![1].free += transQuantity
}

private fun findSeller(
    orderPrev: Order,
    currentOrder: Order,
    minSellerPrice: BigInteger,
    sellerID: Int,
    orderID: Int
): Pair<BigInteger, Int> {
    var minSellerPrice1 = minSellerPrice
    var sellerID1 = sellerID
    if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
        if (orderPrev.price < minSellerPrice1) {
            minSellerPrice1 = orderPrev.price
            sellerID1 = orderID
        }
    }
    return Pair(minSellerPrice1, sellerID1)
}

