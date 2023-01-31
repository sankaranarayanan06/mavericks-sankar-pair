package com.example.controller

import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.BuyOrderHistory
import com.example.model.SellOrderHistory
import com.example.model.Transaction
import com.example.services.generateErrorResponse
import com.example.services.performESOPVestings
import com.example.validations.user.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable


@Controller("/user")
class OrderHistory() {
    @Get("/{username}/order")
    fun getOrderHistroy(@PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            performESOPVestings(username)
            var listOfOrders = mutableListOf<Any>()
            for ((key,order) in orderList) {
                var orderID: Int
                var quantity: Long = 0
                if (username == order!!.userName ) {
                    orderID = order!!.orderId
                    var listOfTransactions = mutableListOf<Transaction>()
                    for (transaction in transactions[orderID]!!) {
                        listOfTransactions.add(transaction)
                        // quantity += eachTrans.first
                    }

                    if (order!!.type == "BUY") {
                        listOfOrders.add(
                            BuyOrderHistory(
                                orderId = orderID + 1,
                                price = order!!.price,
                                quantity = order!!.placedQuantity,
                                type = order!!.type,
                                status = order!!.status,
                                filled = listOfTransactions
                            )
                        )
                    } else {
                        listOfOrders.add(
                            SellOrderHistory(
                                orderId = orderID + 1,
                                price = order!!.price,
                                quantity = order!!.placedQuantity,
                                type = order!!.type,
                                status = order!!.status,
                                filled = listOfTransactions,
                                esopType = order!!.esopType
                            )
                        )
                    }
                }

            }

            return HttpResponse.ok(listOfOrders)
        }

        var errorList = mutableListOf<String>("User doesn't exist.")

        return generateErrorResponse(errorList)

    }
}
