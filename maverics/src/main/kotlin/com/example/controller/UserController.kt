package com.example.controller

import com.example.constants.inventoryData
import com.example.constants.vestingHistory
import com.example.constants.vestings
import com.example.model.Inventory
import com.example.model.User
import com.example.model.Wallet
import com.example.model.allUsers
import com.example.model.*
import com.example.validations.registerValidation
import com.example.validations.user.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class UserController {
    @Post("/register")
    fun register(@Body body: JsonObject): HttpResponse<*> {
        val errorList = mutableListOf<String>()
        val errorResponse = mutableMapOf<String, MutableList<String>>()


        try {
            errorList += registerValidation(
                body["userName"],
                body["firstName"],
                body["lastName"],
                body["phoneNumber"],
                body["email"]
            )
            errorResponse["errors"] = errorList
            if (errorList.size > 0) {
                return HttpResponse.badRequest(errorResponse)
            }

            val firstName = body["firstName"]!!.stringValue
            var lastName = body["lastName"]!!.stringValue
            var phoneNumber = body["phoneNumber"]!!.stringValue
            var email = body["email"]!!.stringValue
            var username = body["userName"]!!.stringValue
            val newUser = User(firstName, lastName, phoneNumber, email, username)
            val successBody = mutableListOf<String>()

            allUsers[username] = newUser
            inventoryData[username] = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
            walletList[username] = Wallet()
            vestings.put(username, mutableListOf())
            vestingHistory.put(username, mutableListOf())
            successBody.add("User added successfully")
            return HttpResponse.ok(successBody)
        } catch (e: Exception) {
            e.printStackTrace()
            errorResponse["error"] = (listOf("An unknown error occurred.").toMutableList())
            return HttpResponse.badRequest(errorResponse);
        }
    }
}
