package com.example.services

import com.example.model.BuyOrderResponse
import com.example.model.Order
import com.example.model.SellOrderResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigInteger

class OrderServiceTest{

    var orderService:OrderService = OrderService()
    @Test
    fun `it should place a buy order`(){
        createUser("anushka","joshi","1234567890","aushka@sahaj.ai","anushka")
        val order = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","anushka","")
        val expectedResponse = BuyOrderResponse(BigInteger.valueOf(10),BigInteger.valueOf(10),"unfilled","BUY",1)

        val response = orderService.placeBuyOrder(order)

        assertEquals(expectedResponse,response["orderDetails"])
    }

    @Test
    fun `it should place a sell order`(){
        createUser("sankar","m","1234678904","sankar@sahaj.ai","sankar")
        val order = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"SELL","sankar","PERFORMANCE")
        val expectedResponse = SellOrderResponse(1,BigInteger.valueOf(10),BigInteger.valueOf(10),"unfilled","SELL","PERFORMANCE")

        val response = orderService.placeSellOrder(order)

        assertEquals(expectedResponse,response["orderDetails"])
    }
}