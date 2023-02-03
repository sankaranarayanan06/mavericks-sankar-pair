package com.example.controller

import com.example.constants.inventoryData
import com.example.constants.vestingHistory
import com.example.constants.vestings
import com.example.model.Inventory
import com.example.model.User
import com.example.model.Wallet
import com.example.constants.allUsers
import com.example.constants.*
import com.example.model.*
import com.example.validations.registerValidation
import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
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
            val lastName = body["lastName"]!!.stringValue
            val phoneNumber = body["phoneNumber"]!!.stringValue
            val email = body["email"]!!.stringValue
            val username = body["userName"]!!.stringValue
            val newUser = User(firstName, lastName, phoneNumber, email, username)
            val response = mutableMapOf<String, String>()

            allUsers[username] = newUser
            inventoryData[username] =
                mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
            walletList[username] = Wallet()
            vestings[username] = mutableListOf()
            vestingHistory[username] = mutableListOf()
            response["message"] = "User $username registered successfully"
            return HttpResponse.ok(response)
        } catch (e: Exception) {
            e.printStackTrace()
            errorResponse["error"] = (listOf("An unknown error occurred.").toMutableList())
            return HttpResponse.badRequest(errorResponse)
        }
    }
}
