package com.example.services

import com.example.constants.*
import com.example.controller.walletList
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


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

        inventoryData[user.userName] = mutableListOf(Inventory(0, 0), Inventory(0, 0))
        walletList[user.userName]!!.freeAmount = 200

        val buyOrder = Order()
        buyOrder.currentQuantity = 1
        buyOrder.placedQuantity = 1
        buyOrder.price = 100
        buyOrder.type = "BUY"
        buyOrder.userName = user.userName

        // Act [1]
        val orderResponse = addBuyOrder(buyOrder)

        // Assert [2]
        assertEquals(null, orderResponse["errors"])
        assertEquals(100, walletList[user.userName]!!.lockedAmount)
        assertEquals(100, walletList[user.userName]!!.freeAmount)

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

        inventoryData[user.userName] = mutableListOf(Inventory(10, 0), Inventory(0, 0))
        val sellOrder = Order()
        sellOrder.currentQuantity = 5
        sellOrder.placedQuantity = 5
        sellOrder.price = 100
        sellOrder.type = "SELL"
        sellOrder.esopType = "PERFORMANCE"
        sellOrder.userName = user.userName

        // Act [1]
        val orderResponse = addSellOrder(sellOrder)

        // Assert [2]
        assertEquals(null, orderResponse["errors"])
        assertEquals(5, inventoryData[user.userName]!![0].free)
        assertEquals(5, inventoryData[user.userName]!![0].locked)

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

        val sellOrder = Order()
        sellOrder.currentQuantity = 5
        sellOrder.placedQuantity = 5
        sellOrder.price = 10
        sellOrder.type = "SELL"
        sellOrder.esopType = "PERFORMANCE"
        sellOrder.userName = user1.userName

        val user2 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            username = "user2",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user2)
        walletList[user2.userName]!!.freeAmount = 200

        val buyOrder = Order()
        buyOrder.currentQuantity = 5
        buyOrder.placedQuantity = 5
        buyOrder.price = 10
        buyOrder.type = "BUY"
        buyOrder.userName = user2.userName

        // Act
        val sellOrderResponse = addSellOrder(sellOrder)
        val buyOrderResponse = addBuyOrder(buyOrder)

        // Assert [2]
        assertEquals(null, sellOrderResponse["errors"])
        assertEquals(null, buyOrderResponse["errors"])
        assertEquals(50, walletList[user1.userName]!!.freeAmount)
        assertEquals(5, inventoryData[user1.userName]!![0].free)
        assertEquals(100, walletList[user2.userName]!!.freeAmount)
        assertEquals(5, inventoryData[user2.userName]!![1].free)
    }
}
