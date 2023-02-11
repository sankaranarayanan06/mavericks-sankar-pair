package com.example.wallet

import com.example.constants.Limits
import com.example.model.Order
import com.example.model.User
import com.example.services.OrderService
import com.example.services.WalletHandler
import com.example.services.addUser
import com.example.validations.WalletValidation
import com.example.validations.order.validateOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

class WalletTest {
    private var orderService = OrderService()
    @BeforeEach
    fun createdUser() {
        Order.orderIdCounter = 1
        val user = User(
            firstName = "Anushka",
            lastName = "Joshi",
            userName = "03Anushka",
            email = "anushka@sahaj.ai",
            phoneNumber = "9359290177"
        )
        addUser(user)
    }

    @Test
    fun `It should test for insufficient amount in wallet`() {
        val buyerName = "03Anushka"
        val order = Order(BigInteger.valueOf(100),BigInteger.ONE,"BUY",buyerName)
        WalletHandler.addFreeAmountInWallet(buyerName, BigInteger.valueOf(90))
        val expectedResponse = listOf("Insufficient amount in wallet")

        val orderResponse = validateOrder(order,buyerName)

        Assertions.assertEquals(expectedResponse, orderResponse)
    }

    @Test
    fun `It should test wallet amount`() {
        val buyerName = "03Anushka"
        WalletHandler.addFreeAmountInWallet(buyerName, BigInteger.valueOf(500))
        val order = Order(BigInteger.valueOf(100),BigInteger.ONE,"BUY",buyerName)

        orderService.placeBuyOrder(order,buyerName)

        Assertions.assertEquals(BigInteger.valueOf(100), WalletHandler.getLockedAmount(buyerName))
        Assertions.assertEquals(BigInteger.valueOf(400), WalletHandler.getFreeAmount(buyerName))

    }

    @Test
    fun `It should test for maximum wallet amount limit`() {
        val buyerName = "03Anushka"
        val walletValidation = WalletValidation()
        val buyOrder = Order()
        buyOrder.remainingQuantity = BigInteger.ONE
        buyOrder.quantity = buyOrder.remainingQuantity
        buyOrder.price = BigInteger.valueOf(100)

        WalletHandler.addFreeAmountInWallet(buyerName, Limits.MAX_WALLET_AMOUNT)

        val freeAmount = WalletHandler.getFreeAmount(buyerName)
        val orderResponse = walletValidation.validations(freeAmount, buyOrder.remainingQuantity * buyOrder.price)


        Assertions.assertEquals(
            "[Maximum wallet limit of amount ${Limits.MAX_WALLET_AMOUNT} would be exceeded]",
            orderResponse.toString()
        )

    }

}
