package com.example.controller

import com.example.constants.*
import com.example.dto.OrderDTO
import com.example.exception.ErrorResponseBodyException
import com.example.model.Order
import com.example.model.OrderResponse
import com.example.model.ResponseBody
import com.example.services.*
import com.example.validations.isUserExists
import com.example.validations.order.validateOrder
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post


@Controller("/user")
class OrderController {

    private var orderService = OrderService()

    @Post("/{username}/order")
    fun addNewOrder(@Body body: OrderDTO, @PathVariable username: String): HttpResponse<ResponseBody> {
        if (!isUserExists(username)) {
            throw ErrorResponseBodyException(listOf("User does not exist."))
        }

        val currentOrder = Order(body.price, body.quantity, body.type, username)
        val errors = validateOrder(currentOrder, username)
        if (errors.size > 0) {
            throw ErrorResponseBodyException(errors)
        }

        val response: OrderResponse = if (currentOrder.type == "BUY") {
            orderService.placeBuyOrder(currentOrder, username)
        } else {
            orderService.placeSellOrder(currentOrder, body.esopType!!, username)
        }

        return HttpResponse.ok(ResponseBody(response))
    }
}
