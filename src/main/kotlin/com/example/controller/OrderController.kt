package com.example.controller

import com.example.constants.*
import com.example.model.Order
import com.example.model.OrderResponse
import com.example.services.*
import com.example.validations.isUserExists
import com.example.validations.order.ifSufficientAmountInWallet
import com.example.validations.order.orderValidation
import com.example.validations.order.orderoverflowValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class OrderController {
    @Post("/{username}/order")
    fun addNewOrder(@Body body: JsonObject, @PathVariable username: String): HttpResponse<*> {
        if (!isUserExists(username)) {
            return generateErrorResponse(mutableListOf("User does not exist"))
        }

        val currentOrder = getOrderFromBody(body, username)

        val errors = validateOrder(currentOrder, username)
        if (errors.size > 0) {
            return generateErrorResponse(errors)
        }

        val response: MutableMap<String, OrderResponse>

        val orderService = OrderService()
        if (currentOrder.type == "BUY") {
            response = orderService.placeBuyOrder(currentOrder)
        } else{
            if (body["esopType"]!!.stringValue == "PERFORMANCE") {
                currentOrder.esopType = "PERFORMANCE"
            }
            response = orderService.placeSellOrder(currentOrder)
        }
        return HttpResponse.ok(response)
    }

    private fun validateOrder(
        currentOrder: Order,
        username: String
    ): MutableList<String> {
        val errorList = mutableListOf<String>()
        orderValidation(
            errorList,
            currentOrder.placedQuantity,
            currentOrder.type,
            currentOrder.price
        )

        orderoverflowValidation(
            errorList,
            username,
            currentOrder.placedQuantity,
            currentOrder.price,
            currentOrder.type
        )

        if (!ifSufficientAmountInWallet(username, currentOrder.currentQuantity * currentOrder.price)) {
            errorList.add("Insufficient amount in wallet")
        }
        return errorList
    }

    private fun getOrderFromBody(body: JsonObject, username: String): Order {
        return Order(
            body["price"]!!.bigIntegerValue,
            body["quantity"]!!.bigIntegerValue,
            body["type"]!!.stringValue,
            userName = username
        )
    }
}
