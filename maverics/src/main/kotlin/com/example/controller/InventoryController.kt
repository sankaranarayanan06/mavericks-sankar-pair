package com.example.controller

import com.example.constants.inventoryData
import com.example.constants.inventoryList
import com.example.constants.response
import com.example.model.Message
import com.example.validations.InventoryValidation
import com.example.validations.user.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class InventoryController() {
    @Post("/{username}/inventory")
    fun addEsopInInventory(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            var inventoryValidation = InventoryValidation()
            var quantityToAdd: Long = 0L
            var type: String = ""
            val inventoryError = mutableListOf<String>()

            try {
                quantityToAdd = body["quantity"].longValue
                type = body["type"].stringValue
            } catch(e: Exception) {
                response["error"] = mutableListOf<String>("Please enter both type(String) and quantity(Number)")
                return HttpResponse.ok(response)
            }


            inventoryList = inventoryData[username]!!
            inventoryValidation.validation(inventoryError,inventoryList[0], inventoryList[1], quantityToAdd, type)
            if(inventoryError.size > 0) {
                response["error"] = inventoryError
                return HttpResponse.ok(response)
            }

            if (type == "PERFORMANCE") {
                inventoryList[0].free += quantityToAdd
            } else if (type == "NON_PERFORMANCE") {
                inventoryList[1].free += quantityToAdd
            }

            inventoryData[username] = inventoryList

            return HttpResponse.ok(Message("$quantityToAdd $type ESOPs added to account"))
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
//            val userInventory = inventoryData[username]
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
