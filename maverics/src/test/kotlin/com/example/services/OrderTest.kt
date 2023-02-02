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
            userName = "dnyaneshwar",
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
            userName = "dnyaneshwar",
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

        println(orderList[sellOrder.orderId]!!.status + " hello")

        // Assert [2]
        assertEquals(null, orderResponse["errors"])
        assertEquals(BigInteger.valueOf(5), inventoryData[user.userName]!![0].free)
        assertEquals(BigInteger.valueOf(5), inventoryData[user.userName]!![0].locked)
        assertEquals("unfilled", orderList[sellOrder.orderId]!!.status)

    }

    @Test
    fun `it should match the buy order to existing sell order`() {
        // Arrange
        val user1 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user1",
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
            userName = "user2",
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
        assertEquals("filled", orderList[buyOrder.orderId]!!.status)
        assertEquals("filled", orderList[sellOrder.orderId]!!.status)
    }

    @Test
    fun `It should partial fill a sell order to existing buy order`() {
        // Arrange
        val user1 = User(
            firstName = "Amaan",
            lastName = "Shaikh",
            userName = "user1",
            email = "amaanshaikh@gmail.com",
            phoneNumber = "9764919739"
        )
        addUser(user1)
        walletList[user1.userName]?.freeAmount = (5000).toBigInteger()

        val buyOrder = Order()
        buyOrder.currentQuantity = (500).toBigInteger()
        buyOrder.placedQuantity = (500).toBigInteger()
        buyOrder.price = (10).toBigInteger()
        buyOrder.type = "BUY"
        buyOrder.userName = user1.userName



        val user2 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user2",
            email = "dnyaneshwarware@gmail.com",
            phoneNumber = "9021118815"
        )
        addUser(user2)
        inventoryData[user2.userName]!![0].free = (300).toBigInteger()

        val sellOrder = Order()
        sellOrder.currentQuantity = (200).toBigInteger()
        sellOrder.placedQuantity = (200).toBigInteger()
        sellOrder.price = (10).toBigInteger()
        sellOrder.type = "SELL"
        sellOrder.esopType = "PERFORMANCE"
        sellOrder.userName = user2.userName


        // Act
        val buyOrderResponse = addBuyOrder(buyOrder)
        val sellOrderResponse = addSellOrder(sellOrder)

        // Assert [2]
        assertEquals(null, sellOrderResponse["errors"])
        assertEquals(null, buyOrderResponse["errors"])
        assertEquals(BigInteger.ZERO, walletList[user1.userName]!!.freeAmount)
        assertEquals((3000).toBigInteger(), walletList[user1.userName]!!.lockedAmount)
        assertEquals((200).toBigInteger(), inventoryData[user1.userName]!![1].free)
        assertEquals((100).toBigInteger(), inventoryData[user2.userName]!![0].free)
        assertEquals((2000).toBigInteger(), walletList[user2.userName]!!.freeAmount)
        assertEquals(1, orderList[1]!!.orderId)
        assertEquals("partially filled", orderList[buyOrder.orderId]!!.status)
        assertEquals("filled", orderList[sellOrder.orderId]!!.status)
    }

    @Test
    fun `It should partial fill a buy order to existing sell order`() {
        // Arrange
        val user1 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user2",
            email = "dnyaneshwarware@gmail.com",
            phoneNumber = "9021118815"
        )
        addUser(user1)
        inventoryData[user1.userName]!![1].free = (500).toBigInteger()

        val sellOrder = Order()
        sellOrder.currentQuantity = (200).toBigInteger()
        sellOrder.placedQuantity = (200).toBigInteger()
        sellOrder.price = (10).toBigInteger()
        sellOrder.type = "SELL"
        sellOrder.esopType = "NON_PERFORMANCE"
        sellOrder.userName = user1.userName



        val user2 = User(
            firstName = "Amaan",
            lastName = "Shaikh",
            userName = "user1",
            email = "amaanshaikh@gmail.com",
            phoneNumber = "9764919739"
        )
        addUser(user2)
        walletList[user2.userName]?.freeAmount = (5000).toBigInteger()

        val buyOrder = Order()
        buyOrder.currentQuantity = (300).toBigInteger()
        buyOrder.placedQuantity = (300).toBigInteger()
        buyOrder.price = (10).toBigInteger()
        buyOrder.type = "BUY"
        buyOrder.userName = user2.userName



        // Act
        val sellOrderResponse = addSellOrder(sellOrder)
        val buyOrderResponse = addBuyOrder(buyOrder)

        // Assert [2]
        assertEquals(null, sellOrderResponse["errors"])
        assertEquals(null, buyOrderResponse["errors"])
        assertEquals((300).toBigInteger(), inventoryData[user1.userName]!![1].free)
        assertEquals(BigInteger.ZERO, inventoryData[user1.userName]!![1].locked)
        assertEquals((1960).toBigInteger(), walletList[user1.userName]!!.freeAmount)
        assertEquals(BigInteger.ZERO, walletList[user1.userName]!!.lockedAmount)
         assertEquals((2000).toBigInteger(), walletList[user2.userName]!!.freeAmount)
        assertEquals((1000).toBigInteger(), walletList[user2.userName]!!.lockedAmount)
        assertEquals((200).toBigInteger(), InventoryHandler.getFreeNonPerformanceInventory(user2.userName))
        assertEquals(BigInteger.ZERO, InventoryHandler.getLockedNonPerformanceInventory(user2.userName))
        assertEquals("filled", orderList[sellOrder.orderId]!!.status)
        assertEquals("partially filled", orderList[buyOrder.orderId]!!.status)
    }
}
