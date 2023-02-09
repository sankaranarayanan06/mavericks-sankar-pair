package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.Order
import com.example.model.Transaction
import java.math.BigInteger

class PerformOrder {
    fun performSells(seller: Order, sellerUser: String) {
        while (true) {
            if (seller.currentQuantity == BigInteger.ZERO){
                break
            }

            val buyerOrderId = findBestBuyOrder(seller)
            if(buyerOrderId != null){
                val buyer = orderList[buyerOrderId]
                val buyPrice = buyer!!.price

                val orderExecutionQuantity: BigInteger = buyer.getMinimumQuantity(seller)

                buyer.currentQuantity -= orderExecutionQuantity
                seller.currentQuantity -= orderExecutionQuantity

                // Return amount for high buy low sell scenario
                val returnAmount: BigInteger = ((buyPrice - seller.price) * orderExecutionQuantity)
                walletList[buyer.userName]!!.lockedAmount -= returnAmount
                walletList[buyer.userName]!!.freeAmount += returnAmount

                // Get seller amount added to seller's account
                // Reduce the locked amount from buyer account
                // Add ESOPs to buyer account
                val platformCharge = calculatePlatformFee(seller, orderExecutionQuantity * seller.price)
                addPlatformCharge(platformCharge)

                walletList[sellerUser]!!.freeAmount += (orderExecutionQuantity * seller.price - platformCharge!!)
                walletList[buyer.userName]!!.lockedAmount -= (orderExecutionQuantity * seller.price)


                inventoryData[buyer.userName]!![1].free += orderExecutionQuantity
                if (seller.esopType == "PERFORMANCE")
                    inventoryData[sellerUser]!![0].locked -= orderExecutionQuantity
                else
                    inventoryData[sellerUser]!![1].locked -= orderExecutionQuantity

                if (!transactions.containsKey(seller.orderId)) {
                    transactions[seller.orderId] = mutableListOf()
                }

                transactions[seller.orderId]!!
                    .add(Transaction(orderExecutionQuantity, seller.price, orderList[seller.orderId]!!.esopType))

                if (!transactions.containsKey(buyer.orderId)) {
                    transactions[buyer.orderId] = mutableListOf()
                }

                transactions[buyerOrderId]!!
                    .add(Transaction(orderExecutionQuantity, seller.price, orderList[seller.orderId]!!.esopType))

                seller.updateStatus()
                orderList[buyerOrderId]?.updateStatus()

            } else{
                break
            }

        }
    }

    private fun findBestBuyOrder(seller: Order): Int?{
        val buyList = orderList.filter { (orderId,order) -> order.type == "BUY"}
        if(buyList.size <= 0){
            return null
        }
        var maxBuyer = BigInteger.ZERO
        var orderId:Int = 0
        for ((id,order) in buyList){
            if(order.price > maxBuyer){
                maxBuyer = order.price
                orderId = id
            }
        }
        return orderId
    }

    private fun calculatePlatformFee(seller: Order, orderTotal: BigInteger): BigInteger? {
        val platformCharge =
            if (orderList[seller.orderId]!!.esopType != "PERFORMANCE") (orderTotal * BigInteger.TWO) / BigInteger.valueOf(
                100
            ) else BigInteger.ZERO
        return platformCharge
    }

    fun performBuys(currentOrder: Order, username: String) {
        while (true) {
            if (currentOrder.currentQuantity == BigInteger.ZERO){
                break
            }

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

                //Update Status of the order
                val seller = orderList[sellerID]
                seller?.updateStatus()
                currentOrder.updateStatus()

            } else {
                break
            }

        }
    }

    fun updateTransactionDetails(orderID: Int, transQuantity: BigInteger, minSellerPrice: BigInteger){
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
}