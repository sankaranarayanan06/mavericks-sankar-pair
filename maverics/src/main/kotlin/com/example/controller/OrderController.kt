package com.example.controller

import com.example.constants.*
import com.example.model.Order
import com.example.services.generateErrorResponse
import com.example.services.performBuys
import com.example.services.performSells
import com.example.validations.order.checkOrderInputs
import com.example.validations.UserValidation
import com.example.validations.isValidESOPType
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
        var errorList = mutableListOf<String>()
        if (UserValidation.isUserExist(username)) {
            var currentOrder = Order()


            checkOrderInputs(errorList,body["quantity"].longValue, body["type"].stringValue, body["quantity"].longValue)



            if(errorList.size > 0){
                return generateErrorResponse(errorList)
            }


                currentOrder.currentQuantity = body["quantity"].longValue;
                currentOrder.placedQuantity = currentOrder.currentQuantity
                currentOrder.type = body["type"].stringValue;
                currentOrder.price = body["price"].longValue;
                currentOrder.status = "unfilled";
                currentOrder.userName = username;



            orderValidation(errorList, currentOrder.placedQuantity, currentOrder.type, currentOrder.price)

            if(errorList.size > 0) {
                return generateErrorResponse(errorList)
            }



            var n = orderList.size

            if (currentOrder.type == "BUY") {
                // Buyer section
                var orderAmount = currentOrder.price * currentOrder.currentQuantity;
                if (!OrderValidation().ifSufficientAmountInWallet(username, orderAmount)) {
                    errorList.add("Insufficient amont in wallet")
                    return generateErrorResponse(errorList);
                }
                currentOrder.orderId = orderID++;
                orderList.add(currentOrder);
                transactions.put(orderID-1, mutableListOf<Pair<Long,Long>>())

                // Locking amount for order placing
                walletList.get(username)!!.lockedAmount += (currentOrder.currentQuantity * currentOrder.price)
                walletList.get(username)!!.freeAmount -= (currentOrder.currentQuantity * currentOrder.price)

                performBuys(currentOrder,username)

                var response = HashMap<String, Any>();

                response["userName"] = currentOrder.userName
                response["quantity"] = currentOrder.placedQuantity
                response["price"] = currentOrder.price
                response["type"] = currentOrder.type

                return HttpResponse.ok(response);

            } else if(currentOrder.type == "SELL") {


                try {
                    currentOrder.esopType = body["esopType"].stringValue

                } catch (e: Exception) {
                    errorList.add("Enter ESOP type")
                    return generateErrorResponse(errorList+











                    )
                }
                if(!isValidESOPType(currentOrder.esopType)){
                    println("Here")
                    errorList.add("Invalid ESOP Type")

                    return generateErrorResponse(errorList);
                }

                if (!OrderValidation().ifSufficientQuantity(username, currentOrder.currentQuantity,currentOrder.esopType)) {
                    errorList.add("Insufficient quantity of ESOPs")

                    return generateErrorResponse(errorList)
                }

                currentOrder.orderId = orderID++;
                orderList.add(currentOrder)
                transactions.put(orderID-1, mutableListOf<Pair<Long,Long>>())

                // Locking
                if(currentOrder.esopType == "PERFORMANCE"){
                    inventoryList[0].free -= body["quantity"].longValue
                    inventoryList[0].locked += body["quantity"].longValue
                }
                else if(currentOrder.esopType == "NON_PERFORMANCE")
                {
                    inventoryList[1].free -= body["quantity"].longValue
                    inventoryList[1].locked += body["quantity"].longValue
                }
                else
                {
                    val response = mutableMapOf<String, MutableList<String>>();
                    var errorList = mutableListOf<String>("Invalid ESOP Type")
                    response["error"] = errorList;
                    return HttpResponse.badRequest(response);
                }

                performSells(currentOrder,username)

                var response = HashMap<String, Any>();

                response["userName"] = currentOrder.userName
                response["quantity"] = currentOrder.placedQuantity
                response["price"] = currentOrder.price
                response["type"] = currentOrder.type

                return HttpResponse.ok(response);

            }

            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("Invalid Order Type")
            response["error"] = errorList;
            return HttpResponse.badRequest(response);



        } else {
            errorList.add("User doesn't exist.")
            return generateErrorResponse(errorList)
        }

    }
}
