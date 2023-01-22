package com.example.controller

import com.example.model.Message
import com.example.model.OrderHistory
import com.example.model.Transaction
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable


@Controller("/user")
class OrderHistory(){
    @Get("/{username}/order")
    fun getOrderHistroy(@PathVariable username: String): HttpResponse<*> {
        if(UserValidation.isUserExist(username)) {
            var listOfOrders= mutableListOf<OrderHistory>()
            for (i in 0 until orderList.size) {
                var orderID :Int
                var quantity:Long=0
                if(username== orderList[i].userName)
                {
                    orderID= orderList[i].orderId
                    var listOfTransactions= mutableListOf<Transaction>()
                    println(orderID);
                    for(eachTrans in transactions[orderID]!!)
                    {
                        listOfTransactions.add(Transaction(eachTrans.first,eachTrans.second))
                        // quantity += eachTrans.first
                    }
                    listOfOrders.add(com.example.model.OrderHistory(orderID + 1, orderList[i].price,orderList[i].placedQuantity,orderList[i].type,
                        orderList[i].esopType,listOfTransactions))
                }
            }

            return HttpResponse.ok(listOfOrders)

        }
        else
        {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }
}
