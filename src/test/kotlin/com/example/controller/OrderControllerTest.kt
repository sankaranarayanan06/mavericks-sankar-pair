package com.example.controller

import com.example.constants.allUsers
import com.example.constants.orderList
import com.example.dto.OrderDTO
import com.example.model.*
import com.example.services.InventoryHandler
import com.example.services.OrderService
import com.example.services.WalletHandler
import com.example.services.addUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

@MicronautTest
class OrderControllerTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    var orderService = OrderService()

    private val buyerUser = User(
        firstName = "Anushkar",
        lastName = "Joshi",
        userName = "anushka",
        email = "anushkaj@sahaj.ai",
        phoneNumber = "4234234763"
    )
    private val sellerUser = User(
        firstName = "Dnyaneshwar",
        lastName = "Ware",
        userName = "dnyaneshwar",
        email = "as@sahaj.ai",
        phoneNumber = "4234234123"
    )

    @BeforeEach
    fun clearData() {
        Order.orderIdCounter = 1
        allUsers.clear()
        orderList.clear()
        addUser(buyerUser)
        addUser(sellerUser)
    }

    @Test
    fun `Place a valid sell order`() {
        // Arrange
        InventoryHandler.addToNonPerformanceInventory(BigInteger.valueOf(100), sellerUser.userName)
        val order = OrderDTO(BigInteger.TEN, BigInteger.TEN,"SELL","PERFORMANCE")
        val expectedResponse = OrderResponse(orderId = 1, price = BigInteger.TEN, quantity = BigInteger.TEN, type = "SELL")
        val sellOrder = Order(BigInteger.TEN,BigInteger.TEN,"SELL",sellerUser.userName,"PERFORMANCE")

        // Act
        val request = HttpRequest.POST(
            "/user/${sellerUser.userName}/order", order
        )

        // Assert
        val response = client.toBlocking().retrieve(request)


        assertEquals(
            expectedResponse,
            orderService.placeSellOrder(sellOrder,sellOrder.esopType,sellerUser.userName)
        )
    }
    @Test
    fun `Place a valid buy order`() {
        // Arrange
        WalletHandler.addFreeAmountInWallet(sellerUser.userName, BigInteger.valueOf(100))
        val order = OrderDTO(BigInteger.TEN, BigInteger.TEN,"BUY","")
        val expectedResponse = OrderResponse(orderId = 1, price = BigInteger.TEN, quantity = BigInteger.TEN, type = "BUY")
        val buyOrder = Order(BigInteger.TEN,BigInteger.TEN,"BUY",buyerUser.userName)

        // Act
        val request = HttpRequest.POST(
            "/user/${sellerUser.userName}/order", order
        )

        // Assert
        val response = client.toBlocking().retrieve(request)


        assertEquals(
            expectedResponse,
            orderService.placeBuyOrder(buyOrder,buyerUser.userName)
        )
    }
}
