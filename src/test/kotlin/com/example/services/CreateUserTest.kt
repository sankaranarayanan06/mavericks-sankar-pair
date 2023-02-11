package com.example.services

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.controller.walletList
import com.example.model.order.Order
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateUserTest {
    @BeforeEach
    fun `Clear Inventory, users, wallets, order`() {
        Order.orderIdCounter = 0
        inventoryData.clear()
        allUsers.clear()
        walletList.clear()
        orderList.clear()
    }

    @Test
    fun `it should add valid user`() {
        val firstName = "Satyam"
        val lastName = "baldawa"
        val userName = "sat"
        val email = "sat@gmail.com"
        val phoneNumber = "8983517226"

        createUser(firstName, lastName, phoneNumber, email, userName)

        assertEquals(allUsers[userName]!!.userName, userName)
        assertEquals(walletList[userName]!!.freeAmount, 0.toBigInteger())
        assertEquals(walletList[userName]!!.lockedAmount, 0.toBigInteger())
    }
}