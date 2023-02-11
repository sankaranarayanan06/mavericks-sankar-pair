package com.example.controller

import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.BuyOrderHistory
import com.example.model.ErrorResponse
import com.example.model.SellOrderHistory
import com.example.model.Transaction
import com.example.services.performESOPVestings
import com.example.validations.isUserExists
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable


@Controller("/user")
class OrderHistory() {
    @Get("/{username}/order")
    fun getOrderHistory(@PathVariable username: String): HttpResponse<*> {
        if (isUserExists(username)) {
            performESOPVestings(username)
            val listOfOrders = mutableListOf<Any>()
            for ((_,order) in orderList) {
                var orderID: Int

                if (username == order.userName ) {
                    orderID = order.orderId
                    val listOfTransactions = mutableListOf<Transaction>()
                    for (transaction in transactions[orderID]!!) {
                        listOfTransactions.add(transaction)
                    }

                    if (order.type == "BUY") {
                        listOfOrders.add(
                            BuyOrderHistory(
                                orderId = orderID,
                                price = order.price,
                                quantity = order.quantity,
                                type = order.type,
                                status = order.status,
                                filled = listOfTransactions
                            )
                        )
                    } else {
                        listOfOrders.add(
                            SellOrderHistory(
                                orderId = orderID + 1,
                                price = order.price,
                                quantity = order.quantity,
                                type = order.type,
                                status = order.status,
                                filled = listOfTransactions,
                                esopType = order.esopType
                            )
                        )
                    }
                }

            }

            return HttpResponse.ok(listOfOrders)
        }

        return HttpResponse.badRequest(ErrorResponse(listOf("User doesn't exist.")))

    }
}
