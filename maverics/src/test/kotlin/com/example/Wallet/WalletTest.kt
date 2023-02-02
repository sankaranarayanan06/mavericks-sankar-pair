package com.example.Wallet

import com.example.constants.Limits
import com.example.model.Order
import com.example.model.User
import com.example.services.WalletHandler
import com.example.services.addBuyOrder
import com.example.services.addUser
import com.example.validations.WalletValidation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

class WalletTest {
    @BeforeEach
    fun createdUser() {
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

        val buyOrder = Order()
        buyOrder.currentQuantity = BigInteger.ONE
        buyOrder.placedQuantity = BigInteger.ONE
        buyOrder.price = BigInteger.valueOf(100)
        buyOrder.type = "BUY"
        buyOrder.userName = buyerName

        WalletHandler.addFreeAmountInWallet(buyerName, BigInteger.valueOf(90))

        val orderResponse = addBuyOrder(buyOrder)

        Assertions.assertEquals("[Insufficient amount in wallet]", orderResponse["errors"].toString())


    }

    @Test
    fun `It should test wallet amount`() {


        val buyerName = "03Anushka"
        val buyOrder = Order()
        buyOrder.currentQuantity = BigInteger.ONE
        buyOrder.placedQuantity = BigInteger.ONE
        buyOrder.price = BigInteger.valueOf(100)
        buyOrder.type = "BUY"
        buyOrder.userName = buyerName

        WalletHandler.addFreeAmountInWallet(buyerName, BigInteger.valueOf(500))

        val orderResponse = addBuyOrder(buyOrder)

        Assertions.assertEquals(null, orderResponse["errors"])
        Assertions.assertEquals(BigInteger.valueOf(100), WalletHandler.getLockedAmount(buyerName))
        Assertions.assertEquals(BigInteger.valueOf(400), WalletHandler.getFreeAmount(buyerName))

    }

    @Test
    fun `It should test for maximum wallet amount limit`() {
        val buyerName = "03Anushka"
        val walletValidation = WalletValidation()
        val buyOrder = Order()
        buyOrder.currentQuantity = BigInteger.ONE
        buyOrder.placedQuantity = buyOrder.currentQuantity
        buyOrder.price = BigInteger.valueOf(100)

        WalletHandler.addFreeAmountInWallet(buyerName, Limits.MAX_WALLET_AMOUNT)

        var freeAmount = WalletHandler.getFreeAmount(buyerName)
        val orderResponse = walletValidation.validations(freeAmount, buyOrder.currentQuantity * buyOrder.price)


        Assertions.assertEquals(
            "[Maximum wallet limit of amount ${Limits.MAX_WALLET_AMOUNT} would be exceeded]",
            orderResponse.toString()
        )

    }

}
