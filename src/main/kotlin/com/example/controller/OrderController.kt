package com.example.controller

import com.example.constants.*
import com.example.dto.OrderDTO
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
class OrderController() {

    var orderService =  OrderService()
    @Post("/{username}/order")
    fun addNewOrder(@Body body:OrderDTO, @PathVariable username: String): HttpResponse<ResponseBody> {
        if (!isUserExists(username)) {
            return HttpResponse
                .badRequest(ResponseBody(listOf("User does not exist."),null))
        }

        val currentOrder = Order(body.price,body.quantity,body.type,username)
        val errors = validateOrder(currentOrder, username)
        if (errors.size > 0) {
            return HttpResponse.badRequest(ResponseBody(errors,null))
        }

        val response: OrderResponse = if (currentOrder.type == "BUY") {
            orderService.placeBuyOrder(body,username)
        } else {
            orderService.placeSellOrder(body, username)
        }
        return HttpResponse.ok(ResponseBody(null,response))
    }
}
