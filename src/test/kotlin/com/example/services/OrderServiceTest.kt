package com.example.services

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.controller.walletList
import com.example.dto.OrderDTO
import com.example.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

class OrderServiceTest{

    private var orderService:OrderService = OrderService()

    @BeforeEach
    fun setup(){
        orderList.clear()
        allUsers.clear()
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
    @Test
    fun `it should match the buy order for existing sell order`(){
        createUser("anushka","joshi","6427847644","anushkaj@sahaj.ai","anushka")
        val sellerName = "anushka"
        val sellOrderDetails = OrderDTO(BigInteger.valueOf(10),BigInteger.valueOf(10),"SELL","PERFORMANCE")
        InventoryHandler.addToPerformanceInventory(BigInteger.valueOf(10),sellerName)



        createUser("sankar","m","1234568790","sankar@sahaj.ai","sankar")
        val buyOrderDetails = OrderDTO(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","")
        val buyerName = "sankar"
        WalletHandler.addFreeAmountInWallet(buyerName,BigInteger.valueOf(500))

        orderService.placeSellOrder(sellOrderDetails,sellerName)
        orderService.placeBuyOrder(buyOrderDetails, buyerName)

        assertEquals(BigInteger.valueOf(100),WalletHandler.getFreeAmount(sellerName))
        assertEquals(BigInteger.ZERO,InventoryHandler.getFreePerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(0), WalletHandler.getLockedAmount(buyerName))
        assertEquals(BigInteger.valueOf(400), WalletHandler.getFreeAmount(buyerName))

    }
    @Test
    fun `it should match the buy order to existing sell order`() {
        // Arrange
        val user1 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user1",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user1)

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, user1.userName)

        inventoryData[user1.userName] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        val sellOrder = OrderDTO(BigInteger.valueOf(10), BigInteger.valueOf(5), "SELL", "PERFORMANCE")
        val sellerName = "user1"
        orderService.placeSellOrder(sellOrder, sellerName)


        val user2 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user2",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user2)

        walletList[user2.userName]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = OrderDTO(BigInteger.valueOf(10), BigInteger.valueOf(5), "BUY","")
        val buyerName = "user2"


        // Act
        orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(50), walletList[user1.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(user1.userName))
        assertEquals(BigInteger.valueOf(450), walletList[user2.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(user2.userName))
    }

    @Test
    fun `It should partial fill a buy order to existing sell order`() {
        // Arrange
        val user1 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user1",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user1)

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, user1.userName)

        inventoryData[user1.userName] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO,"NON_PERFORMANCE"))
        val sellOrder = OrderDTO(BigInteger.valueOf(10), BigInteger.valueOf(5), "SELL", "PERFORMANCE")
        val sellerName = "user1"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellerName)


        val user2 = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "user2",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(user2)

        walletList[user2.userName]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = OrderDTO(BigInteger.valueOf(10), BigInteger.valueOf(10), "BUY","")
        val buyerName = "user2"


        // Act
        val buyOrderResponse = orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(400), walletList[user2.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(50), walletList[user1.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(user1.userName))
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(user2.userName))
        assertEquals("partially filled", orderList[buyOrderResponse.orderId]!!.status)
        assertEquals("filled", orderList[sellOrderResponse.orderId]!!.status)
    }

    @Test
    fun `It should partial fill a sell order to existing buy order`() {
        // Arrange
        val buyer = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "buyer",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(buyer)
        walletList[buyer.userName]!!.freeAmount = BigInteger.valueOf(500)
        val buyOrder = OrderDTO(BigInteger.valueOf(10), BigInteger.valueOf(10), "BUY", "")
        val buyerName = "buyer"
        val buyOrderResponse = orderService.placeBuyOrder(buyOrder, buyerName)

        val seller = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "seller",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(seller)
        InventoryHandler.addToPerformanceInventory(BigInteger.valueOf(30), seller.userName)
        val sellOrder = OrderDTO(BigInteger.valueOf(10), BigInteger.valueOf(20), "SELL", "PERFORMANCE")
        val sellerName = "seller"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellerName)


        // Assert [2]
        assertEquals(BigInteger.valueOf(100), walletList[sellerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(400), walletList[buyerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreePerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(buyerName))
        assertEquals("partially filled", orderList[sellOrderResponse.orderId]!!.status)
        assertEquals("filled", orderList[buyOrderResponse.orderId]!!.status)

    }
}
