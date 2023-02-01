package com.example.services

import com.example.constants.inventoryData
import com.example.controller.walletList
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class WalletTest {
    @Test
    fun `It should test wallet amount`(){
        val user = User(
            firstName = "Anushka",
            lastName = "Joshi",
            username = "03Anushka",
            email = "anushka@sahaj.ai",
            phoneNumber = "9359290177"
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
        var orderResponse = addBuyOrder(buyOrder)

        Assertions.assertEquals(null, orderResponse["errors"])
        Assertions.assertEquals(100, walletList[user.userName]!!.lockedAmount)
        Assertions.assertEquals(0, walletList[user.userName]!!.freeAmount)

    }
}