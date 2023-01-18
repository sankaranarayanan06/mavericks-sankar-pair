package com.example.controller
import com.example.model.Inventory
import com.example.model.Message
import com.example.model.Wallet
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject


var inventorMap = HashMap<String,Inventory>()

@Controller("/user")
class InventoryController(){
    @Post("/{username}/inventory")
    fun addEsopInInventory(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if(UserValidation.isUserExist(username)) {
            val quantityToAdd = body["quantity"].intValue
            inventorMap[username]!!.freeESOP += quantityToAdd
            val userInventory = inventorMap[username]

            return HttpResponse.ok(Message("${quantityToAdd} ESOPS added to account.\n Total free quantity is ${userInventory!!.freeESOP}\n" +
                    "Total locked quantity is ${userInventory!!.lockESOP}"))
        }
        else
        {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }

    @Get("/{username}/inventory")
    fun getInventory(@PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {

            val userInventory = inventorMap[username]

            val response = mutableMapOf<String, Int>();

            if (userInventory != null) {
                response["Free ESOP"] = userInventory.freeESOP
            };

            if (userInventory != null) {
                response["Locked ESOP"] = userInventory.lockESOP
            };

            return HttpResponse.ok(response)
        } else {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }
}