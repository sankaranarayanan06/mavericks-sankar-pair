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


@Controller("/user")
class InventoryController() {
    @Post("/{username}/inventory")
    fun addEsopInInventory(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            val response = mutableMapOf<String, MutableList<String>>();

            val quantityToAdd: Long = body["quantity"].longValue

            val type: String = body["type"].stringValue

            inventoryList = inventorMap[username]!!

            if (type == "PERFORMANCE") {
                inventoryList[0].free += quantityToAdd
            } else if (type == "NON_PERFORMANCE") {
                inventoryList[1].free += quantityToAdd
            } else {

                var errorList = mutableListOf<String>("Wrong ESOP type!")
                response["error"] = errorList;
                return HttpResponse.ok(response);
            }

            inventorMap[username] = inventoryList



            return HttpResponse.ok(Message("${quantityToAdd}  $type ESOPS added to account"))
        } else {
            val response = mutableMapOf<String, MutableList<String>>();
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
