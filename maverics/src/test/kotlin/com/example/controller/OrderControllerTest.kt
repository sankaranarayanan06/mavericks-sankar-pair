package com.example.controller

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.OrderRequest
import com.example.model.User
import com.example.services.addUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

@MicronautTest
class OrderControllerTest {


    @Inject
    @field:Client("/")
    lateinit var client: HttpClient


    val buyer = User(
        firstName = "Dnyaneshwar",
        lastName = "Ware",
        userName = "dnyaneshwar",
        email = "as",
        phoneNumber = "4234234"
    )

    @BeforeEach
    fun clearData() {
        allUsers.clear()
        orderList.clear()

        addUser(buyer)
    }

    @Test
    fun `Place a valid buy order`(objectMapper: ObjectMapper) {
        // Arrange
        inventoryData[buyer.userName] =
            mutableListOf(Inventory(BigInteger.ZERO, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        walletList[buyer.userName]!!.freeAmount = BigInteger.valueOf(200)
        val buyOrder = OrderRequest("BUY", BigInteger.TEN, BigInteger.TEN)

        // Act
        val request = HttpRequest.POST(
            "/user/${buyer.userName}/order", objectMapper.writeValueAsString(buyOrder)
        )

        // Assert
        val response = client.toBlocking().retrieve(request)

        assertEquals(
            "{\"quantity\":10,\"orderId\":1,\"price\":10,\"userName\":\"dnyaneshwar\",\"type\":\"BUY\"}",
            response
        )


    }

    @Test
    fun `place a buy order with insufficient amount in user wallet`(objectMapper: ObjectMapper) {
        // Arrange
        inventoryData[buyer.userName] =
            mutableListOf(Inventory(BigInteger.ZERO, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        walletList[buyer.userName]!!.freeAmount = BigInteger.valueOf(20)
        val buyOrder = OrderRequest("BUY", BigInteger.TEN, BigInteger.TEN)

        // Act
        val request = HttpRequest.POST(
            "/user/${buyer.userName}/order", objectMapper.writeValueAsString(buyOrder)
        )

        // Assert

        val exception: HttpClientResponseException = org.junit.jupiter.api.assertThrows {
            client.toBlocking().retrieve(request)
        }

        println(exception.message)



    }
}
