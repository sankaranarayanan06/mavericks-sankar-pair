package com.example.controller

import com.example.constants.allUsers
import com.example.constants.orderList
import com.example.model.*
import com.example.services.InventoryHandler
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


    private val buyerUser = User(
        firstName = "Dnyaneshwar",
        lastName = "Ware",
        userName = "dnyaneshwar",
        email = "as",
        phoneNumber = "4234234"
    )
    private val sellerUser = User(
        firstName = "Dnyaneshwar",
        lastName = "Ware",
        userName = "dnyaneshwar",
        email = "as",
        phoneNumber = "4234234"
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
    fun `Place a valid sell order`(objectMapper: ObjectMapper) {
        // Arrange
        InventoryHandler.addToNonPerformanceInventory(BigInteger.valueOf(100), sellerUser.userName)

        val sellOrder = OrderRequest("SELL", BigInteger.TEN, BigInteger.TEN, esopType = "NON_PERFORMANCE")
        val expectedResponse = SellOrderResponse(orderId = 1, price = BigInteger.TEN, quantity = BigInteger.TEN, status = "unfilled", type = "SELL", esopType = "NON_PERFORMANCE")


        // Act
        val request = HttpRequest.POST(
            "/user/${buyerUser.userName}/order", objectMapper.writeValueAsString(sellOrder)
        )

        // Assert
        val response = client.toBlocking().retrieve(request)


        assertEquals(
            expectedResponse,
            objectMapper.readValue(response.toString(), SellOrderResponse::class.java)
        )
    }

    @Test
    fun `Place a valid buy order`(objectMapper: ObjectMapper) {
        // Arrange
        walletList[buyerUser.userName]!!.freeAmount = BigInteger.valueOf(200)
        val buyOrder = OrderRequest("BUY", BigInteger.TEN, BigInteger.TEN)
        val buyOrderResponse = BuyOrderResponse(BigInteger.TEN, BigInteger.TEN, "unfilled", "BUY", 1)

        // Act
        val request = HttpRequest.POST(
            "/user/${buyerUser.userName}/order", objectMapper.writeValueAsString(buyOrder)
        )
        val response = client.toBlocking().retrieve(request)

        // Assert
        assertEquals(
            buyOrderResponse,
            objectMapper.readValue(response, BuyOrderResponse::class.java)
        )


    }

    @Test
    fun `place a buy order with insufficient amount in user wallet`(objectMapper: ObjectMapper) {
        // Arrange
        walletList[buyerUser.userName]!!.freeAmount = BigInteger.valueOf(20)
        val buyOrder = OrderRequest("BUY", BigInteger.TEN, BigInteger.TEN)

        // Act
        val request = HttpRequest.POST(
            "/user/${buyerUser.userName}/order", objectMapper.writeValueAsString(buyOrder)
        )

        // Assert

        val exception: HttpClientResponseException = org.junit.jupiter.api.assertThrows {
            client.toBlocking().retrieve(request)
        }

        println(exception.message)


    }
}
