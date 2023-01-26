package com.example.controller

import com.example.constants.inventoryData
import com.example.constants.vestingHistory
import com.example.constants.vestings
import com.example.model.Inventory
import com.example.model.allUsers
import com.example.services.performESOPVestings
import com.example.validations.user.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/user")
class AccuntInformation {

    @Get("/{username}/accountInformation")
    fun accountInformation(@PathVariable username: String): HttpResponse<*> {
        if(UserValidation.isUserExist(username)) {
            performESOPVestings(username)

            val inventoryList: MutableList<Inventory> = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
            val user = allUsers.get(username)
            val userWallet = walletList[username]
            inventoryList[0] = inventoryData[username]?.get(0)!!
            inventoryList[1] = inventoryData[username]?.get(1)!!

            val response = mutableMapOf<String, Any>()
            val walletInfo = mutableMapOf<String, Long>()

            walletInfo["free"] = userWallet!!.freeAmount
            walletInfo["locked"] = userWallet.lockedAmount

            response["firstName"] = user!!.firstName
            response["lastName"] = user.lastName
            response["phoneNumber"] = user.phoneNumber
            response["email"] = user.email

            response["wallet"] = walletInfo
            response["inventory"] = inventoryList
            response["pendingVestings"] = vestings[username]!!
            response["vestingHistory"] = vestingHistory[username]!!

            return HttpResponse.ok(response)

        }
        else
        {
            val errorResponse = mutableMapOf<String, MutableList<String>>()
            val errorList = mutableListOf<String>("User doesn't exist.")
            errorResponse["error"] = errorList
            return HttpResponse.badRequest(errorResponse)
        }
    }}
