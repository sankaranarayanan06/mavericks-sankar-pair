package com.example.controller

import com.example.constants.*
import com.example.model.Order
import com.example.validations.user.UserValidation

import com.example.validations.order.orderValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import com.example.services.*
import com.example.validations.order.orderoverflowValidation


@Controller("/user")
class OrderController {

    @Post("/{username}/order")
    fun addNewOrder(@Body body: JsonObject, @PathVariable username: String): HttpResponse<*> {
        val errorList = mutableListOf<String>()
        if (UserValidation.isUserExist(username)) {
            performESOPVestings(username)

            val currentOrder = Order()

            orderValidation(errorList, body["quantity"]!!.longValue, body["type"]!!.stringValue, body["price"]!!.longValue)
            orderoverflowValidation(errorList, username, body["quantity"]!!.longValue, body["price"]!!.longValue, body["type"]!!.stringValue)

            if(errorList.size > 0){
                return generateErrorResponse(errorList)
            }

            currentOrder.currentQuantity = body["quantity"]!!.longValue
            currentOrder.placedQuantity = currentOrder.currentQuantity
            currentOrder.type = body["type"]!!.stringValue
            currentOrder.price = body["price"]!!.longValue
            currentOrder.status = "unfilled"
            currentOrder.userName = username

            if (currentOrder.type == "BUY") {

                val response = addBuyOrder(currentOrder)

                if(response.containsKey("errors")){
                    return HttpResponse.badRequest(response["errors"])
                }

                performBuys(currentOrder,username)

                return HttpResponse.ok(response)

            } else if(currentOrder.type == "SELL") {

                try {
                    currentOrder.esopType = body["esopType"].stringValue

                } catch (e: Exception) {
                    errorList.add("Enter ESOP type")
                    return generateErrorResponse(errorList)
                }

                val response = addSellOrder(currentOrder)

                if(response.containsKey("errors")){
                    return HttpResponse.badRequest(response["errors"])
                }

                performSells(currentOrder,username)

                return HttpResponse.ok(response)
            }

            val response = mutableMapOf<String, MutableList<String>>()
            response["error"] = mutableListOf<String>("Invalid Order Type")
            return HttpResponse.badRequest(response)

        } else {
            errorList.add("User doesn't exist.")
            return generateErrorResponse(errorList)
        }

    }
}
