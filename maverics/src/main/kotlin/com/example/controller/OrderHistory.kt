package com.example.controller

import com.example.constants.orderList
import com.example.constants.transactions
import com.example.model.BuyOrderHistory
import com.example.model.SellOrderHistory
import com.example.model.Transaction
import com.example.services.generateErrorResponse
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
            var listOfOrders = mutableListOf<Any>()
            for (i in 0 until orderList.size) {
                var orderID: Int
                var quantity: Long = 0
                if (username == orderList[i].userName) {
                    orderID = orderList[i].orderId
                    var listOfTransactions = mutableListOf<Transaction>()
                    for (transaction in transactions[orderID]!!) {
                        listOfTransactions.add(transaction)
                        // quantity += eachTrans.first
                    }

                    if (orderList[i].type == "BUY") {
                        listOfOrders.add(
                            BuyOrderHistory(
                                orderId = orderID + 1,
                                price = orderList[i].price,
                                quantity = orderList[i].placedQuantity,
                                type = orderList[i].type,
                                staus = orderList[i].status,
                                filled = listOfTransactions
                            )
                        )
                    } else {
                        listOfOrders.add(
                            SellOrderHistory(
                                orderId = orderID + 1,
                                price = orderList[i].price,
                                quantity = orderList[i].placedQuantity,
                                type = orderList[i].type,
                                staus = orderList[i].status,
                                filled = listOfTransactions,
                                esopTyoe = orderList[i].esopType
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
