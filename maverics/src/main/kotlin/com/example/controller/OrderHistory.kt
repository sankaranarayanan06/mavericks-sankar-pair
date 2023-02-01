package com.example.controller

import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.BuyOrderHistory
import com.example.model.SellOrderHistory
import com.example.model.Transaction
import com.example.services.generateErrorResponse
import com.example.services.performESOPVestings
import com.example.validations.ifUniqueUsername
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable


@Controller("/user")
class OrderHistory() {
    @Get("/{username}/order")
    fun getOrderHistory(@PathVariable username: String): HttpResponse<*> {
        if (ifUniqueUsername(username)) {
            performESOPVestings(username)
            val listOfOrders = mutableListOf<Any>()
            for ((_,order) in orderList) {
                var orderID: Int
                if (username == order.userName ) {
                    orderID = order.orderId
                    val listOfTransactions = mutableListOf<Transaction>()
                    for (transaction in transactions[orderID]!!) {
                        listOfTransactions.add(transaction)
                        // quantity += eachTrans.first
                    }

                    if (order.type == "BUY") {
                        listOfOrders.add(
                            BuyOrderHistory(
                                orderId = orderID + 1,
                                price = order.price,
                                quantity = order.placedQuantity,
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
                                quantity = order.placedQuantity,
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

        val errorList = mutableListOf("User doesn't exist.")

        return generateErrorResponse(errorList)

    }
}
