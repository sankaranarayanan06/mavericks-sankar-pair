package com.example.controller

import com.example.constants.*
import com.example.dto.OrderDTO
import com.example.model.ResponseBody
import com.example.services.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post


@Controller("/user")
class OrderController {

    private val orderService = OrderService()

    @Post("/{username}/order")
    fun addNewOrder(@Body orderRequest: OrderDTO, @PathVariable username: String): HttpResponse<ResponseBody> {
        val response = orderService.placeOrder(orderRequest, username)
        return HttpResponse.ok(
            ResponseBody(
                response.orderId.toString(),
                response.quantity,
                response.type,
                response.price
            )
        )
    }
}
