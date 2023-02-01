package com.example.services

import com.example.constants.*
import com.example.controller.walletList
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger


class OrderTest {

    @BeforeEach
    fun `Clear Inventory, users, wallets, order`() {
        Order.orderIdCounter = 0
        inventoryData.clear()
        allUsers.clear()
        walletList.clear()
        orderList.clear()
    }

    @Test
    fun `it should place the buy order`() {
        // Arrange [3]

        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            username = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        inventoryData[user.userName] = mutableListOf(Inventory(BigInteger.ZERO, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        walletList[user.userName]!!.freeAmount = BigInteger.valueOf(200)

        val buyOrder = Order()
        buyOrder.currentQuantity = BigInteger.ONE
        buyOrder.placedQuantity = BigInteger.ONE
        buyOrder.price = BigInteger.valueOf(100)
        buyOrder.type = "BUY"
        buyOrder.userName = user.userName

        // Act [1]
        val orderResponse = addBuyOrder(buyOrder)

        // Assert [2]
        assertEquals(null, orderResponse["errors"])
        assertEquals(BigInteger.valueOf(100), walletList[user.userName]!!.lockedAmount)
        assertEquals(BigInteger.valueOf(100), walletList[user.userName]!!.freeAmount)

    }

    @Test
    fun `it should place the sell order`() {
        // Arrange [3]

        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            username = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        inventoryData[user.userName] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        val sellOrder = Order()
        sellOrder.currentQuantity = BigInteger.valueOf(5)
        sellOrder.placedQuantity = BigInteger.valueOf(5)
        sellOrder.price = BigInteger.valueOf(100)
        sellOrder.type = "SELL"
        sellOrder.esopType = "PERFORMANCE"
        sellOrder.userName = user.userName

        // Act [1]
        val orderResponse = addSellOrder(sellOrder)

        // Assert [2]
        assertEquals(null, orderResponse["errors"])
        assertEquals(BigInteger.valueOf(5), inventoryData[user.userName]!![0].free)
        assertEquals(BigInteger.valueOf(5), inventoryData[user.userName]!![0].locked)

    }

    @Test
    fun `it should match the buy order to existing sell order`() {
        // Arrange
        val user1 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            username = "user1",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user1)

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, user1.userName)

        val sellOrder = Order()
        sellOrder.currentQuantity = BigInteger.valueOf(5)
        sellOrder.placedQuantity = BigInteger.valueOf(5)
        sellOrder.price = BigInteger.TEN
        sellOrder.type = "SELL"
        sellOrder.esopType = "PERFORMANCE"
        sellOrder.userName = user1.userName

        addSellOrder(sellOrder)

        val user2 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            username = "user2",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user2)

        walletList[user2.userName]!!.freeAmount = BigInteger.valueOf(200)

        val buyOrder = Order()
        buyOrder.currentQuantity = BigInteger.valueOf(5)
        buyOrder.placedQuantity = BigInteger.valueOf(5)
        buyOrder.price = BigInteger.TEN
        buyOrder.type = "BUY"
        buyOrder.userName = user2.userName

        addBuyOrder(buyOrder)

        // Act
        val sellOrderResponse = addSellOrder(sellOrder)
        val buyOrderResponse = addBuyOrder(buyOrder)

        // Assert [2]
        assertEquals(null, sellOrderResponse["errors"])
        assertEquals(null, buyOrderResponse["errors"])
        assertEquals(BigInteger.valueOf(50), walletList[user1.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(user1.userName))
        assertEquals(BigInteger.valueOf(150), walletList[user2.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(user2.userName))
    }
}
