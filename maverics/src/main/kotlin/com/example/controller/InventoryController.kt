package com.example.controller
import com.example.model.Inventory
import com.example.model.Message
import com.example.model.Wallet
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject


var inventorMap = HashMap<String,Inventory>()

@Controller("/user")
class InventoryController(){
    @Post("/{username}/inventory")
    fun wallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<Message> {
        if(UserValidation.isUserExist(username)) {
            val quantityToAdd = body["quantity"].intValue
            inventorMap[username]!!.freeESOP += quantityToAdd
            val userInventory = inventorMap[username]

            return HttpResponse.ok(Message("${quantityToAdd} ESOPS added to account. Remaining quantity is ${userInventory!!.freeESOP}" +
                    " and remaining locked quantity is ${userInventory!!.lockESOP}"))
        }
        else
        {
            return HttpResponse.ok(Message("User not exist"))
        }
    }
}