package com.example.inventory

import com.example.constants.Limits
import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.controller.walletList
import com.example.model.order.Order
import com.example.model.user.User
import com.example.services.InventoryHandler
import com.example.services.addUser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

class InventoryTest {

    @BeforeEach
    fun `Clear Inventory, users, wallets, order`() {
        Order.orderIdCounter = 0
        inventoryData.clear()
        allUsers.clear()
        walletList.clear()
        orderList.clear()
    }

    @Test
    fun `it should add non-performance type ESOP`() {
        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        InventoryHandler.addToNonPerformanceInventory(BigInteger.valueOf(50), user.userName)

        assertEquals(BigInteger.valueOf(50), InventoryHandler.getFreeNonPerformanceInventory(user.userName))

    }

    @Test
    fun `it should lock non-performance type ESOP`() {
        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        InventoryHandler.lockNonPerformanceInventory(BigInteger.valueOf(50), user.userName)

        assertEquals(BigInteger.valueOf(50), InventoryHandler.getLockedNonPerformanceInventory(user.userName))

    }

    @Test
    fun `it should add performance type ESOP`() {
        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        InventoryHandler.addToPerformanceInventory(BigInteger.valueOf(50), user.userName)

        assertEquals(BigInteger.valueOf(50), InventoryHandler.getFreePerformanceInventory(user.userName))

    }

    @Test
    fun `it should lock performance type ESOP`() {
        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        InventoryHandler.lockPerformanceInventory(BigInteger.valueOf(50), user.userName)

        assertEquals(BigInteger.valueOf(50), InventoryHandler.getLockedPerformanceInventory(user.userName))

    }

    @Test
    fun `it test when inventory quantity exceeds while adding`() {
        val user = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "dnyaneshwar",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user)

        val status =
            InventoryHandler.addToPerformanceInventory(Limits.MAX_INVENTORY_QUANTITY + BigInteger.ONE, user.userName)

        assertEquals(false, status)
    }


}
