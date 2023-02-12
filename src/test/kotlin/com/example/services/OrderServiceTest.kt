package com.example.services

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.orderList
import com.example.controller.walletList
import com.example.model.inventory.EsopType
import com.example.model.inventory.Inventory
import com.example.model.order.Order
import com.example.model.order.OrderStatus
import com.example.model.order.OrderType
import com.example.model.user.User
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
        val dto = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"anushka",OrderType.BUY,EsopType.NONE)
        val expectedResponse = 1
        val username = "anushka"

        orderService.placeBuyOrder(dto, username)

        assertEquals(expectedResponse,orderList.size)
    }

    @Test
    fun `it should place a sell order`(){
        createUser("sankar","m","1234568790","sankar@sahaj.ai","sankar")
        val dto = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"sankar",OrderType.SELL,EsopType.PERFORMANCE)
        val expectedResponse = 1
        val username = "sankar"

        orderService.placeSellOrder(dto,dto.esopType,username)

        assertEquals(expectedResponse,orderList.size)
    }
    @Test
    fun `it should match the buy order for existing sell order`(){
        createUser("anushka","joshi","6427847644","anushkaj@sahaj.ai","anushka")
        val sellerName = "anushka"
        val sellOrderDetails = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"anushka",OrderType.SELL,EsopType.PERFORMANCE)
        InventoryHandler.addToPerformanceInventory(BigInteger.valueOf(10),sellerName)

        createUser("sankar","m","1234568790","sankar@sahaj.ai","sankar")
        val buyOrderDetails = Order(BigInteger.valueOf(10),BigInteger.valueOf(10),"sankar",OrderType.BUY,EsopType.NONE)
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

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, seller.getUserName())

        inventoryData[seller.getUserName()] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO))
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "seller",OrderType.SELL ,EsopType.PERFORMANCE)
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

        walletList[buyer.getUserName()]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "buyer",OrderType.BUY,EsopType.NONE)
        val buyerName = "buyer"


        // Act
        orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(50), walletList[seller.getUserName()]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(seller.getUserName()))
        assertEquals(BigInteger.valueOf(450), walletList[buyer.getUserName()]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(buyer.getUserName()))
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

        InventoryHandler.addToPerformanceInventory(BigInteger.TEN, seller.getUserName())

        inventoryData[seller.getUserName()] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.ZERO, BigInteger.ZERO,EsopType.PERFORMANCE))
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "seller", OrderType.SELL,EsopType.PERFORMANCE)
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

        walletList[buyer.getUserName()]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(10), "buyer",OrderType.BUY,EsopType.NONE)
        val buyerName = "buyer"


        // Act
        val buyOrderResponse = orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(400), walletList[buyer.getUserName()]!!.freeAmount)
        assertEquals(BigInteger.valueOf(50), walletList[seller.getUserName()]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreePerformanceInventory(seller.getUserName()))
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(buyer.getUserName()))
        assertEquals(OrderStatus.PARTIALLY_FILLED, orderList[buyOrderResponse.orderId]!!.status)
        assertEquals(OrderStatus.FILLED, orderList[sellOrderResponse.orderId]!!.status)
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
        walletList[buyer.getUserName()]!!.freeAmount = BigInteger.valueOf(500)
        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(10), "buyer", OrderType.BUY,EsopType.NONE)
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
        InventoryHandler.addToPerformanceInventory(BigInteger.valueOf(30), seller.getUserName())
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(20), "seller", OrderType.SELL,EsopType.PERFORMANCE)
        val sellerName = "seller"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellOrder.esopType,sellerName)


        // Assert [2]
        assertEquals(BigInteger.valueOf(100), walletList[sellerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(400), walletList[buyerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreePerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(buyerName))
        assertEquals(OrderStatus.PARTIALLY_FILLED, orderList[sellOrderResponse.orderId]!!.status)
        assertEquals(OrderStatus.FILLED, orderList[buyOrderResponse.orderId]!!.status)

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

        InventoryHandler.addToNonPerformanceInventory(BigInteger.TEN, seller.getUserName())

        inventoryData[seller.getUserName()] = mutableListOf(Inventory(BigInteger.TEN, BigInteger.ZERO), Inventory(BigInteger.TEN, BigInteger.ZERO))
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "seller", OrderType.SELL,EsopType.NON_PERFORMANCE)
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

        walletList[buyer.getUserName()]!!.freeAmount = BigInteger.valueOf(500)

        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(5), "buyer",OrderType.BUY,EsopType.NONE)
        val buyerName = "buyer"


        // Act
        orderService.placeBuyOrder(buyOrder, buyerName)

        // Assert [2]
        assertEquals(BigInteger.valueOf(49), walletList[seller.getUserName()]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(seller.getUserName()))
        assertEquals(BigInteger.valueOf(450), walletList[buyer.getUserName()]!!.freeAmount)
        assertEquals(BigInteger.valueOf(5), InventoryHandler.getFreeNonPerformanceInventory(buyer.getUserName()))
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
        walletList[buyer.getUserName()]!!.freeAmount = BigInteger.valueOf(500)
        val buyOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(10), "buyer", OrderType.BUY,EsopType.NONE)
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
        InventoryHandler.addToNonPerformanceInventory(BigInteger.valueOf(30), seller.getUserName())
        val sellOrder = Order(BigInteger.valueOf(10), BigInteger.valueOf(20), "seller", OrderType.SELL,EsopType.NON_PERFORMANCE)
        val sellerName = "seller"
        val sellOrderResponse = orderService.placeSellOrder(sellOrder, sellOrder.esopType,sellerName)


        // Assert [2]
        assertEquals(BigInteger.valueOf(98), walletList[sellerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(400), walletList[buyerName]!!.freeAmount)
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(sellerName))
        assertEquals(BigInteger.valueOf(10), InventoryHandler.getFreeNonPerformanceInventory(buyerName))
        assertEquals(OrderStatus.PARTIALLY_FILLED, orderList[sellOrderResponse.orderId]!!.status)
        assertEquals(OrderStatus.FILLED, orderList[buyOrderResponse.orderId]!!.status)

    }

}
