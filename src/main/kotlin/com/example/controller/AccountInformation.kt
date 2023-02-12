package com.example.controller

import com.example.constants.inventoryData
import com.example.model.inventory.EsopType
import com.example.model.inventory.Inventory
import com.example.services.*
import com.example.validations.isUserExists
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import java.util.*

@Controller("/user")
class AccountInformation {

    @Get("/{username}/accountInformation")
    fun accountInformation(@PathVariable username: String): HttpResponse<*> {
        if (isUserExists(username)) {

            performESOPVestings(username)

            val inventoryList: MutableList<Inventory> =
                mutableListOf(Inventory(type = EsopType.PERFORMANCE), Inventory(type = EsopType.NON_PERFORMANCE))
            inventoryList[0] = inventoryData[username]?.get(0)!!
            inventoryList[1] = inventoryData[username]?.get(1)!!

            val response = mutableMapOf<String, Any>()

            response += getUserInfo(username)

            response["wallet"] = WalletHandler.getWalletInfo(username)
            response["inventory"] = InventoryHandler.getInventoryInfo(username)
            response["pendingVestings"] = getPendingVestingInfo(username)
            response["vestingHistory"] = getVestingHistory(username)

            return HttpResponse.ok(response)

        } else {
            val errorResponse = mutableMapOf<String, MutableList<String>>()
            val errorList = mutableListOf<String>("User doesn't exist.")
            errorResponse["error"] = errorList
            return HttpResponse.badRequest(errorResponse)
        }
    }
}
