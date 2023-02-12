package com.example.services

import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.constants.transactions
import com.example.controller.walletList
import com.example.model.order.Order
import com.example.model.history.Transaction
import com.example.model.inventory.EsopType
import com.example.model.order.OrderStatus
import java.math.BigInteger

class PerformOrder {
    fun performSells(seller: Order, sellerUser: String) {
        while (true) {
            if (seller.remainingQuantity == BigInteger.ZERO) {
                break
            }

            var maxBuyerPrice: BigInteger = BigInteger.valueOf(Long.MIN_VALUE)
            var buyerOrderId = Int.MIN_VALUE

            for ((_, orderPrev) in orderList) {
                if (seller.checkIfOrderCanBeMatched(orderPrev, maxBuyerPrice)) {
                    if (orderPrev.price > maxBuyerPrice) {
                        maxBuyerPrice = orderPrev.price
                        buyerOrderId = orderPrev.orderId
                    }
                }
            }

            if (buyerOrderId != Int.MIN_VALUE) {
                val buyer = orderList[buyerOrderId]
                val buyPrice = buyer!!.price

                val orderExecutionQuantity: BigInteger = buyer.getMinimumQuantity(seller)

                buyer.updateExecutedQuantity(orderExecutionQuantity)
                seller.updateExecutedQuantity(orderExecutionQuantity)

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


                inventoryData[buyer.userName]!![1].addFreeEsops(orderExecutionQuantity)
                if (seller.esopType == EsopType.PERFORMANCE)
                    inventoryData[sellerUser]!![0].removeLockedEsops(orderExecutionQuantity)
                else
                    inventoryData[sellerUser]!![1].removeLockedEsops(orderExecutionQuantity)

                if (!transactions.containsKey(seller.orderId)) {
                    transactions[seller.orderId] = mutableListOf()
                }

                transactions[seller.orderId]!!.add(
                    Transaction(
                        orderExecutionQuantity,
                        seller.price,
                        orderList[seller.orderId]!!.esopType
                    )
                )

                if (!transactions.containsKey(buyer.orderId)) {
                    transactions[buyer.orderId] = mutableListOf()
                }

                transactions[buyerOrderId]!!.add(
                    Transaction(
                        orderExecutionQuantity,
                        seller.price,
                        orderList[seller.orderId]!!.esopType
                    )
                )

                seller.updateStatus()
                orderList[buyerOrderId]?.updateStatus()

            } else {
                break
            }

        }
    }

    private fun calculatePlatformFee(seller: Order, orderTotal: BigInteger): BigInteger? {
        return if (orderList[seller.orderId]!!.esopType != EsopType.PERFORMANCE) (orderTotal * BigInteger.TWO) / BigInteger.valueOf(
            100
        ) else BigInteger.ZERO
    }

    fun performBuys(currentOrder: Order, username: String) {
        while (true) {
            if (currentOrder.remainingQuantity == BigInteger.ZERO) {
                break
            }

            var minSellerPrice: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)
            var sellerID = Int.MIN_VALUE

            // Find if seller with PERFORMANCE order fulfils the deal
            for ((orderID, orderPrev) in orderList) {
                if ((orderPrev.esopType == EsopType.PERFORMANCE)) {
                    val pair = findSeller(orderPrev, currentOrder, minSellerPrice, sellerID, orderID)
                    minSellerPrice = pair.first
                    sellerID = pair.second
                }
            }

            // If not found any performance esop seller then go for normal esop seller
            if (sellerID == Int.MIN_VALUE) {
                for ((orderID, orderPrev) in orderList) {
                    if (orderPrev.esopType == EsopType.NON_PERFORMANCE) {
                        val pair = findSeller(orderPrev, currentOrder, minSellerPrice, sellerID, orderID)
                        minSellerPrice = pair.first
                        sellerID = pair.second
                    }

                }
            }
            if (sellerID != Int.MIN_VALUE) {
                val transQuantity = orderList[sellerID]!!.remainingQuantity.min(currentOrder.remainingQuantity)

                orderList[sellerID]!!.remainingQuantity -= transQuantity
                currentOrder.remainingQuantity -= transQuantity

                val orderTotal = minSellerPrice * transQuantity


                val platformCharge =
                    if (orderList[sellerID]!!.esopType != EsopType.PERFORMANCE) (orderTotal * BigInteger.TWO) / BigInteger.valueOf(
                        100
                    ) else BigInteger.ZERO

                addPlatformCharge(platformCharge)

                updateBuyOrderDetails(username, currentOrder, minSellerPrice, transQuantity, sellerID, platformCharge)

                // Update the Order History
                updateTransactionDetails(currentOrder.orderId, transQuantity, minSellerPrice)
                updateTransactionDetails(sellerID, transQuantity, minSellerPrice)

                //Update Status of the order
                val seller = orderList[sellerID]
                seller?.updateStatus()
                currentOrder.updateStatus()

            } else {
                break
            }

        }
    }

    private fun updateTransactionDetails(orderID: Int, transQuantity: BigInteger, minSellerPrice: BigInteger) {
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
        if (orderList[sellerID]!!.esopType == EsopType.PERFORMANCE) {
            inventoryData[orderList[sellerID]!!.userName]!![0].removeLockedEsops(transQuantity)
        } else {
            inventoryData[orderList[sellerID]!!.userName]!![1].removeLockedEsops(transQuantity)
        }

        //Adding ESOP to buyers account
        inventoryData[username]!![1].addFreeEsops(transQuantity)
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
        if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != OrderStatus.FILLED) && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
            if (orderPrev.price < minSellerPrice1) {
                minSellerPrice1 = orderPrev.price
                sellerID1 = orderID
            }
        }
        return Pair(minSellerPrice1, sellerID1)
    }
}