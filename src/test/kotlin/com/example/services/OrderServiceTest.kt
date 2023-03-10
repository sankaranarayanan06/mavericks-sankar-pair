package com.example.services

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.controller.walletList
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.User
import org.junit.jupiter.api.Assertions.assertEquals
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
        val dto = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","anushka")
        val expectedResponse = 1
        val username = "anushka"

        orderService.placeBuyOrder(dto, username)

        assertEquals(expectedResponse,orderList.size)
    }

    @Test
    fun `it should place a sell order`(){
        createUser("sankar","m","1234568790","sankar@sahaj.ai","sankar")
        val dto = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","sankar","PERFORMANCE")
        val expectedResponse = 1
        val username = "sankar"

        orderService.placeSellOrder(dto,dto.esopType,username)

        assertEquals(expectedResponse,orderList.size)
    }
    @Test
    fun `it should match the buy order for existing sell order`(){
        createUser("anushka","joshi","6427847644","anushkaj@sahaj.ai","anushka")
        val sellerName = "anushka"
        val sellOrderDetails = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"SELL","anushka","PERFORMANCE")
        InventoryHandler.addToPerformanceInventory(BigInteger.valueOf(10),sellerName)

        createUser("sankar","m","1234568790","sankar@sahaj.ai","sankar")
        val buyOrderDetails = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"BUY","sankar")
        val buyerName = "sankar"
        WalletHandler.addFreeAmountInWallet(buyerName,BigInteger.valueOf(500))

        //Act
        orderService.placeSellOrder(sellOrderDetails,sellOrderDetails.esopType,sellerName)
        orderService.placeBuyOrder(buyOrderDetails, buyerName)

        //Assert
        assertEquals(BigInteger.valueOf(100),WalletHandler.getFreeAmount(sellerName))
        assertEquals(BigInteger.ZERO,InventoryHandler.getFreePerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(0), WalletHandler.getLockedAmount(buyerName))
        assertEquals(BigInteger.valueOf(400), WalletHandler.getFreeAmount(buyerName))

    }
    @Test
    fun `it should match the buy order to existing sell order`() {
        // Arrange
        val seller = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "seller",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(seller)

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, seller.userName)

        inventoryData[seller.userName] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "SELL", "seller","PERFORMANCE")
        val sellerName = "seller"
        orderService.placeSellOrder(sellOrder,sellOrder.esopType,sellerName)


        val buyer = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "buyer",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(buyer)

        walletList[buyer.userName]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "BUY","buyer")
        val buyerName = "buyer"


        // Act
        orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(50), walletList[seller.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(seller.userName))
        assertEquals(BigInteger.valueOf(450), walletList[buyer.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(buyer.userName))
    }

    @Test
    fun `It should partial fill a buy order to existing sell order`() {
        // Arrange
        val seller = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "seller",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(seller)

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, seller.userName)

        inventoryData[seller.userName] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO,"NON_PERFORMANCE"))
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "SELL", "seller","PERFORMANCE")
        val sellerName = "seller"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellOrder.esopType,sellerName)


        val buyer = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "buyer",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(buyer)

        walletList[buyer.userName]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(10), "BUY","buyer")
        val buyerName = "buyer"


        // Act
        val buyOrderResponse = orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(400), walletList[buyer.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(50), walletList[seller.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(seller.userName))
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(buyer.userName))
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
        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(10), "BUY", "buyer")
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
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(20), "SELL", "seller","PERFORMANCE")
        val sellerName = "seller"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellOrder.esopType,sellerName)


        // Assert [2]
        assertEquals(BigInteger.valueOf(100), walletList[sellerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(400), walletList[buyerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreePerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(buyerName))
        assertEquals("partially filled", orderList[sellOrderResponse.orderId]!!.status)
        assertEquals("filled", orderList[buyOrderResponse.orderId]!!.status)

    }
    @Test
    fun `it should match the buy order to existing sell order of non performance esop type`() {
        // Arrange
        val seller = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "seller",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(seller)

        InventoryHandler.addToNonPerformanceInventory(BigInteger.TEN, seller.userName)

        inventoryData[seller.userName] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.TEN, BigInteger.ZERO))
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "SELL", "seller","NON_PERFORMANCE")
        val sellerName = "seller"
        orderService.placeSellOrder(sellOrder,sellOrder.esopType,sellerName)


        val buyer = User(
            firstName = "Dnyaneshwar",
            lastName = "Ware",
            userName = "buyer",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(buyer)

        walletList[buyer.userName]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "BUY","buyer")
        val buyerName = "buyer"


        // Act
        orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(49), walletList[seller.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(seller.userName))
        assertEquals(BigInteger.valueOf(450), walletList[buyer.userName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(buyer.userName))
    }

    @Test
    fun `It should partially fill a sell order of non performance esop type for existing buy order`() {
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
        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(10), "BUY", "buyer")
        val buyerName = "buyer"
        val buyOrderResponse = orderService.placeBuyOrder(buyOrder, buyerName)

        val seller = User(
            firstName = "Anushka",
            lastName = "Ware",
            userName = "seller",
            email = "as",
            phoneNumber = "4234234"
        )
        addUser(seller)
        InventoryHandler.addToNonPerformanceInventory(BigInteger.valueOf(30), seller.userName)
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(20), "SELL", "seller","NON_PERFORMANCE")
        val sellerName = "seller"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellOrder.esopType,sellerName)


        // Assert [2]
        assertEquals(BigInteger.valueOf(98), walletList[sellerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(400), walletList[buyerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(buyerName))
        assertEquals("partially filled", orderList[sellOrderResponse.orderId]!!.status)
        assertEquals("filled", orderList[buyOrderResponse.orderId]!!.status)

    }

}
