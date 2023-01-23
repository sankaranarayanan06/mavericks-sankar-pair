package com.example.controller

import com.example.model.Inventory
import com.example.model.Message
import com.example.model.Wallet
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject

var inventoryList: MutableList<Inventory> = mutableListOf()
var inventorMap = HashMap<String, MutableList<Inventory>>()
const val maxInventoryQuantity = 100_00_00_000
val response = mutableMapOf<String, MutableList<String>>();


fun inventoryValidation(inventoryError: MutableList<String>,performance: Inventory, nonPerformance: Inventory, quantityToAdd: Long, type: String) {
    if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked > maxInventoryQuantity) {
        inventoryError.add("Quantity out of Range. Max: 100 Crore, Min: 1")
    }
    if (quantityToAdd !in 1..maxInventoryQuantity) {
        inventoryError.add("Amount out of Range. Max: 100 Crore, Min: 1")
    }
    if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked + quantityToAdd > maxInventoryQuantity) {
        inventoryError.add("Inventory limit of 100 crores exceeded")
    }
    if (type != "PERFORMANCE" && type != "NON_PERFORMANCE") {
        inventoryError.add("Wrong ESOP type")
    }
}


@Controller("/user")
class InventoryController() {
    @Post("/{username}/inventory")
    fun addEsopInInventory(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            val quantityToAdd: Long = body["quantity"].longValue
            val type: String = body["type"].stringValue
            val inventoryError = mutableListOf<String>()
            inventoryList = inventorMap[username]!!
            inventoryValidation(inventoryError,inventoryList[0], inventoryList[1], quantityToAdd, type)
            if(inventoryError.size > 0) {
                response["error"] = inventoryError
                return HttpResponse.ok(response)
            }

            if (type == "PERFORMANCE") {
                inventoryList[0].free += quantityToAdd
            } else if (type == "NON_PERFORMANCE") {
                inventoryList[1].free += quantityToAdd
            }

            inventorMap[username] = inventoryList

            return HttpResponse.ok(Message("${quantityToAdd} $type ESOPS added to account"))
        } else {

            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }

//    @Get("/{username}/inventory")
//    fun getInventory(@PathVariable username: String): HttpResponse<*> {
//        if (UserValidation.isUserExist(username)) {
//
//            val userInventory = inventorMap[username]
//
//            val response = mutableMapOf<String, Long>();
//
//            if (userInventory != null) {
//                response["Free ESOP"] = userInventory.free
//            };
//
//            if (userInventory != null) {
//                response["Locked ESOP"] = userInventory.locked
//            };
//
//            return HttpResponse.ok(response)
//        } else {
//            val response = mutableMapOf<String, MutableList<String>>();
//            var errorList = mutableListOf<String>("User doesn't exist.")
//            response["error"] = errorList;s
//
//            return HttpResponse.badRequest(response);
//        }
//    }
}
