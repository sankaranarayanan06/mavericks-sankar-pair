package com.example.controller

import com.example.model.Message
import com.example.model.Wallet
import com.example.model.allUsers
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

@Controller("/user")
class AccuntInformation {

    @Get("/{username}/accountInformation")
    fun accountInformation(@PathVariable username: String): HttpResponse<*> {
        if(UserValidation.isUserExist(username)) {
            var user = allUsers.get(username);
            var userWallet = walletList.get(username);
            var userInventory = inventorMap.get(username);

            var response = mutableMapOf<String, Any>()
            var walletInfo = mutableMapOf<String, Int>()
            var inventoryInfo = mutableMapOf<String, Int>()

            walletInfo["free"] = userWallet!!.freeAmount
            walletInfo["locked"] = userWallet!!.lockedAmount

            inventoryInfo["free"] = userInventory!!.freeESOP
            inventoryInfo["locked"] = userInventory!!.lockESOP



            response["firstName"] = user!!.firstName
            response["lastName"] = user!!.lastName
            response["phoneNumber"] = user!!.phoneNumber
            response["email"] = user!!.email
            
            response["wallet"] = walletInfo
            response["inventory"] = inventoryInfo

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
