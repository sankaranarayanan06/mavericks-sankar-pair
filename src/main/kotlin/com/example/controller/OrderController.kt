package com.example.controller

import com.example.constants.*
import com.example.model.Order

import com.example.validations.order.orderValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import com.example.services.*
import com.example.validations.isUserExists
import com.example.validations.order.orderoverflowValidation


@Controller("/user")
class OrderController {
    @Post("/{username}/order")
    fun addNewOrder(@Body body: JsonObject, @PathVariable username: String): HttpResponse<*> {
        val errorList = mutableListOf<String>()
        if (isUserExists(username)) {
            performESOPVestings(username)


            orderValidation(
                errorList,
                body["quantity"]!!.longValue,
                body["type"]!!.stringValue,
                body["price"]!!.longValue
            )

            orderoverflowValidation(
                errorList,
                username,
                body["quantity"]!!.bigIntegerValue,
                body["price"]!!.bigIntegerValue,
                body["type"]!!.stringValue
            )

            if (errorList.size > 0) {
                return generateErrorResponse(errorList)
            }



            val currentOrder = getOrderFromBody(body, username)

            if (currentOrder.type == "BUY") {

                val response = addBuyOrder(currentOrder)

                if (response is HashMap<*, *> && response.containsKey("errors")) {
                    return HttpResponse.badRequest(response["errors"])
                }

                return HttpResponse.ok(response["orderDetails"])

            } else if (currentOrder.type == "SELL") {

                try {
                    currentOrder.esopType = body["esopType"]!!.stringValue
                } catch (e: Exception) {
                    errorList.add("Enter ESOP type")
                    return generateErrorResponse(errorList)
                }

                val response = addSellOrder(currentOrder)

                if (response.containsKey("errors")) {
                    return HttpResponse.badRequest(response["errors"])
                }

                return HttpResponse.ok(response["orderDetails"])
            }

            val response = mutableMapOf<String, MutableList<String>>()
            response["error"] = mutableListOf("Invalid Order Type")
            return HttpResponse.badRequest(response)

        } else {
            errorList.add("User doesn't exist.")
            return generateErrorResponse(errorList)
        }
    }

    private fun getOrderFromBody(body: JsonObject, username: String): Order {
        return Order(
            body["price"]!!.bigIntegerValue,
            body["quantity"]!!.bigIntegerValue,
            body["quantity"]!!.bigIntegerValue,
            "unfilled",
            body["type"]!!.stringValue,
            userName = username
        )
    }
}
