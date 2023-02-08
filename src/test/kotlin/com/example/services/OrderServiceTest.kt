package com.example.services

import com.example.constants.orderList
import com.example.dto.OrderDTO
import com.example.model.OrderResponse
import com.example.model.Order
import com.example.model.SellOrderResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

class OrderServiceTest{

    var orderService:OrderService = OrderService()

    @BeforeEach
    fun setup(){
        orderList.clear()
    }
    @Test
    fun `it should place a buy order`(){
        createUser("anushka","joshi","1234567890","aushka@sahaj.ai","anushka")
        val dto = OrderDTO(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","")
        val expectedResponse = 1
        val username = "anushka"

        orderService.placeBuyOrder(dto, username)

        assertEquals(expectedResponse,orderList.size)
    }

    @Test
    fun `it should place a sell order`(){
        createUser("sankar","m","1234568790","sankar@sahaj.ai","sankar")
        val dto = OrderDTO(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","PERFORMANCE")
        val expectedResponse = 1
        val username = "sankar"

        orderService.placeSellOrder(dto, username)

        assertEquals(expectedResponse,orderList.size)
    }
}
