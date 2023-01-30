//package com.example
//
//import com.example.constants.orderList
//import com.example.controller.InventoryController
//import com.example.controller.OrderController
//import com.example.controller.UserController
//import com.example.model.InventoryRequest
//import com.example.model.OrderRequest
//import com.example.model.User
//import com.fasterxml.jackson.annotation.JsonCreator
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.databind.annotation.JsonSerialize
//import io.micronaut.json.JsonMapper
//import io.micronaut.json.tree.JsonObject
//import io.micronaut.test.extensions.junit5.annotation.MicronautTest
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//
//@MicronautTest
//class OrdersTest {
//
//    @Test
//    fun `It place a buy order`() {
//        // ARRANGE
//        val order = OrderRequest(type = "BUY", price = 15, quantity = 20, esopType = "NON_PERFORMANCE")
//        val user = User(
//            email = "amaan.shaikh@gmail.com",
//            firstName = "Amaan",
//            lastName = "Shaikh",
//            phoneNumber = "9764919739",
//            userName = "u1"
//        )
//        val inventory = InventoryRequest(quantity = 50, type = "NON_PERFORMANCE")
//        val userName = "u1"
//
//        val userController = UserController()
//        val orderController = OrderController()
//        val inventoryController = InventoryController()
//        val mapper = ObjectMapper()
//        val orderObject: JsonObject = mapper.readValue(mapper.writeValueAsString(order), JsonObject::class.java)
//        val userObject: JsonObject = mapper.readValue(mapper.writeValueAsString(user), JsonObject::class.java)
//
//
//        // ACT [1]
//        userController.register(userObject)
//        inventoryController.addEsopInInventory(userName, inventory)
//        orderController.addNewOrder(orderObject, userName)
//        // ASSERT
//        Assertions.assertEquals("unfilled", orderList[0].status)
//        Assertions.assertEquals(1, orderList[0].orderId)
//    }
//
//}