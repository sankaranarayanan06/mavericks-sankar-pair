package com.example.controller

import com.example.constants.inventoryData
import com.example.constants.inventoryList
import com.example.constants.response
import com.example.model.Message
import com.example.services.addESOPVestings
import com.example.services.performESOPVestings
import com.example.validations.InventoryValidation
import com.example.validations.user.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class InventoryController {
    @Post("/{username}/inventory")
    fun addEsopInInventory(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            performESOPVestings(username)

            val inventoryValidation = InventoryValidation()
            var quantityToAdd: Long
            var type: String
            val inventoryError = mutableListOf<String>()

            try {
                quantityToAdd = body["quantity"]?.longValue!!
                type = body["type"]?.stringValue!!
            } catch (e: Exception) {
                response["error"] = mutableListOf("Please enter both type(String) and quantity(Number)")
                return HttpResponse.ok(response)
            }


            inventoryList = inventoryData[username]!!
            inventoryValidation.validation(inventoryError, inventoryList[0], inventoryList[1], quantityToAdd, type)
            if (inventoryError.size > 0) {
                response["error"] = inventoryError
                return HttpResponse.ok(response)
            }

            if (type == "PERFORMANCE") {
                inventoryList[0].free += quantityToAdd
            } else if (type == "NON_PERFORMANCE") {
                // Vesting period for non performance esops

                addESOPVestings(username, quantityToAdd, type)

                HttpResponse.ok(Message("$quantityToAdd $type ESOP adding request submitted. It will reflect to your account according to vesting period"))
            }

            inventoryData[username] = inventoryList

            return HttpResponse.ok(Message("$quantityToAdd $type ESOPs added to account"))
        } else {

            val errorList = mutableListOf("User doesn't exist.")
            response["error"] = errorList

            return HttpResponse.badRequest(response)
        }
    }

}
