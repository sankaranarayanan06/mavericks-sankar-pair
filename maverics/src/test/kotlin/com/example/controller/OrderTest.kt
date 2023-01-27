package com.example.controller

import com.example.constants.*
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.User
import com.example.services.addBuyOrder
import com.example.services.addSellOrder
import com.example.services.addUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class OrderTest {

    @BeforeEach
    fun `Clear Inventory, users, wallets, order`() {
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
        buyOrder.orderId = 0
        buyOrder.currentQuantity = 1
        buyOrder.placedQuantity = 1
        buyOrder.price = 100
        buyOrder.type = "BUY"
        buyOrder.userName = user.userName

        // Act [1]
        var orderResponse = addBuyOrder(buyOrder)

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
        sellOrder.orderId = 0
        sellOrder.currentQuantity = 5
        sellOrder.placedQuantity = 5
        sellOrder.price = 100
        sellOrder.type = "SELL"
        sellOrder.esopType = "PERFORMANCE"
        sellOrder.userName = user.userName

        // Act [1]
        var orderResponse = addSellOrder(sellOrder)

        // Assert [2]
        assertEquals(null, orderResponse["errors"])
        assertEquals(5, inventoryData[user.userName]!![0].free)
        assertEquals(5, inventoryData[user.userName]!![0].locked)

    }


}
