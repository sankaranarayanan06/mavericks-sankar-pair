package com.example.controller

import com.example.model.Inventory
import com.example.model.Message
import com.example.model.Wallet
import com.example.model.allUsers
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import java.lang.reflect.Type

@Controller("/user")
class AccuntInformation {

    @Get("/{username}/accountInformation")
    fun accountInformation(@PathVariable username: String): HttpResponse<*> {
        if(UserValidation.isUserExist(username)) {
            var inventoryList: MutableList<Inventory> = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
            var user = allUsers.get(username);
            var userWallet = walletList.get(username);
            inventoryList[0] = inventorMap[username]?.get(0)!!
            inventoryList[1] = inventorMap[username]?.get(1)!!



            var response = mutableMapOf<String, Any>()
            var walletInfo = mutableMapOf<String, Long>()
//            var inventoryInfo = mutableMapOf<String, Long>()


            walletInfo["free"] = userWallet!!.freeAmount
            walletInfo["locked"] = userWallet!!.lockedAmount

//            inventoryInfo["free"] = inventoryList[0]!!.free
//            inventoryInfo["locked"] = inventoryList[0]!!.locked



            response["firstName"] = user!!.firstName
            response["lastName"] = user!!.lastName
            response["phoneNumber"] = user!!.phoneNumber
            response["email"] = user!!.email

            response["wallet"] = walletInfo
            response["inventory"] = inventoryList

            return HttpResponse.ok(response)

        }
        else
        {
            val errorResponse = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            errorResponse["error"] = errorList;
            return HttpResponse.badRequest(errorResponse);
        }
    }}
