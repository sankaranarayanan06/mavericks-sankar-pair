package com.example.controller

import com.example.model.Inventory
import com.example.model.Order
import com.example.services.performBuys
import com.example.services.performSells
import com.example.validations.OrderValidation
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject

var orderList = mutableListOf<Order>()
var orderID = 0;

var transactions: MutableMap<Int, MutableList<Pair<Long, Long>>> = mutableMapOf()
/// quantity--price

@Controller("/user")
class OrderController {

    @Post("/{username}/order")
    fun addNewOrder(@Body body: JsonObject, @PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            var currentOrder = Order()

            var transT: MutableList<Pair<Long, Long>> = mutableListOf()


            currentOrder.currentQuantity = body["quantity"].longValue;
            currentOrder.placedQuantity = currentOrder.currentQuantity
            currentOrder.type = body["type"].stringValue;
            currentOrder.price = body["price"].longValue;
            currentOrder.status = "unfilled";
            currentOrder.userName = username;


            var n = orderList.size

            if (currentOrder.type == "BUY") {
                // Buyer section
                var orderAmount = currentOrder.price * currentOrder.currentQuantity;
                // validations
                if (!OrderValidation().ifSufficientAmountInWallet(username, orderAmount)) {
                    val response = mutableMapOf<String, MutableList<String>>();
                    var errorList = mutableListOf<String>("Insufficient amount in wallet")
                    response["error"] = errorList;

                    return HttpResponse.badRequest(response);
                }
                currentOrder.orderId = orderID++;
                orderList.add(currentOrder);
                n = orderList.size


                // Locking amount for order placing
                walletList.get(username)!!.lockedAmount += (currentOrder.currentQuantity * currentOrder.price)
                walletList.get(username)!!.freeAmount -= (currentOrder.currentQuantity * currentOrder.price)

                performBuys(currentOrder,username)

            } else {

                var quantity = body["quantity"].longValue

                if (!OrderValidation().ifSufficientQuantity(username, currentOrder.currentQuantity)) {
                    val response = mutableMapOf<String, MutableList<String>>();
                    var errorList = mutableListOf<String>("Insufficient quantity of ESOPs")
                    response["error"] = errorList;

                    return HttpResponse.badRequest(response);
                }

                var inventoryList: MutableList<Inventory> = inventorMap[username]!!

                var orderOne: Order = Order()
                var orderTwo: Order = Order()


                if (inventoryList[0].free >= body["quantity"].longValue) {

                    println("first")
                    inventoryList[0].free -= body["quantity"].longValue
                    inventoryList[0].locked += body["quantity"].longValue

                    orderOne.orderId = orderID++
                    orderOne.currentQuantity = quantity
                    orderOne.placedQuantity = quantity
                    orderOne.price = body["price"].longValue
                    orderOne.type = body["type"].stringValue
                    orderOne.status = "unfilled"
                    orderOne.userName = username
                    orderOne.esopType = "PERFORMANCE"

                    orderList.add(orderOne);
                    transactions.put(orderID - 1, transT);

                    println("Placed Quantity" + currentOrder.placedQuantity)

                    performSells(orderOne, username)


                } else {
                    var quantityFirst = inventoryList[0].free
                    var quantitySecond = currentOrder.currentQuantity - quantityFirst


                    inventoryList[0].free -= quantityFirst
                    inventoryList[0].locked += quantityFirst


                    inventoryList[1].free -= quantitySecond
                    inventoryList[1].locked += quantitySecond

                    if(quantityFirst > 0) {
                        orderOne.orderId = orderID++
                        orderOne.currentQuantity = quantityFirst
                        orderOne.placedQuantity = quantityFirst
                        orderOne.price = body["price"].longValue
                        orderOne.type = body["type"].stringValue
                        orderOne.status = "unfilled"
                        orderOne.userName = username
                        orderOne.esopType = "PERFORMANCE"

                        orderList.add(orderOne);
                        transactions.put(orderID - 1, mutableListOf<Pair<Long, Long>>());
                        performSells(orderOne, username)
                    }

                    orderTwo.orderId = orderID++
                    orderTwo.currentQuantity = quantitySecond
                    orderTwo.placedQuantity = quantitySecond
                    orderTwo.price = body["price"].longValue
                    orderTwo.type = body["type"].stringValue
                    orderTwo.status = "unfilled"
                    orderTwo.userName = username
                    orderTwo.esopType = "NON_PERFORMANCE"
                    orderList.add(orderTwo);
                    transactions.put(orderID - 1, mutableListOf<Pair<Long, Long>>());
                    performSells(orderTwo, username)

                }

            }

            var response = HashMap<String, Any>();

            response["userName"] = currentOrder.userName
            response["quantity"] = currentOrder.placedQuantity
            response["price"] = currentOrder.price
            response["type"] = currentOrder.type

            return HttpResponse.ok(response);
        } else {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;
            return HttpResponse.badRequest(response);
        }

    }
}
