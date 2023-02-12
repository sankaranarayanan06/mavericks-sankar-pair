package com.example.wallet

import com.example.constants.Limits
import com.example.model.inventory.EsopType
import com.example.model.order.Order
import com.example.model.order.OrderType
import com.example.model.user.User
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
        val order = Order(BigInteger.valueOf(100),BigInteger.ONE,buyerName,OrderType.BUY,EsopType.NONE)
        WalletHandler.addFreeAmountInWallet(buyerName, BigInteger.valueOf(90))
        val expectedResponse = listOf("Insufficient amount in wallet")

        val orderResponse = validateOrder(order,buyerName)

        Assertions.assertEquals(expectedResponse, orderResponse)
    }

    @Test
    fun `It should test wallet amount`() {
        val buyerName = "03Anushka"
        WalletHandler.addFreeAmountInWallet(buyerName, BigInteger.valueOf(500))
        val order = Order(BigInteger.valueOf(100),BigInteger.ONE,buyerName,OrderType.BUY,EsopType.NONE)

        orderService.placeBuyOrder(order,buyerName)

        Assertions.assertEquals(BigInteger.valueOf(100), WalletHandler.getLockedAmount(buyerName))
        Assertions.assertEquals(BigInteger.valueOf(400), WalletHandler.getFreeAmount(buyerName))

    }

    @Test
    fun `It should test for maximum wallet amount limit`() {
        val buyerName = "03Anushka"
        val walletValidation = WalletValidation()
        val buyOrder = Order(BigInteger.valueOf(100),BigInteger.ONE,buyerName,OrderType.BUY,EsopType.NONE)


        WalletHandler.addFreeAmountInWallet(buyerName, Limits.MAX_WALLET_AMOUNT)

        val freeAmount = WalletHandler.getFreeAmount(buyerName)
        val orderResponse = walletValidation.validations(freeAmount, buyOrder.remainingQuantity * buyOrder.price)


        Assertions.assertEquals(
            "[Maximum wallet limit of amount ${Limits.MAX_WALLET_AMOUNT} would be exceeded]",
            orderResponse.toString()
        )

    }

}
