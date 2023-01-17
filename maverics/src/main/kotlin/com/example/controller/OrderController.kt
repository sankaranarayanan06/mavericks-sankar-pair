package com.example.controller

import com.example.model.Order
import com.example.model.User
import com.example.validations.OrderValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import java.lang.Integer.min

var orderList= mutableListOf<Order>()
var orderID=0;

var transactions: MutableMap<Int,MutableList<Pair<Int,Int>>> =  mutableMapOf()
/// quantity--price

@Controller("/user")
class OrderController {

    @Post("/{username}/order")
    fun register(@Body body: JsonObject,@PathVariable username:String): HttpResponse<*> {
        var currentOrder = Order()

        currentOrder.orderId = orderID;
        orderID++;
        currentOrder.quantity = body["quantity"].intValue;
        currentOrder.type = body["type"].stringValue;
        currentOrder.price = body["price"].intValue;
        currentOrder.status = "unfilled";
        currentOrder.userName = username;

        orderList.add(currentOrder);


        var n = orderList.size

        if (currentOrder.type == "BUY") {
            var orderAmount = currentOrder.price * currentOrder.quantity;

            if (!OrderValidation().ifSufficientAmountInWallet(username, orderAmount)) {
                val response = mutableMapOf<String, MutableList<String>>();
                var errorList = mutableListOf<String>("Insufficient amount in wallet")
                response["error"] = errorList;

                return HttpResponse.badRequest(response);
            }

            walletList.get(username)!!.lockedAmount+=(currentOrder.quantity*currentOrder.price)
            walletList.get(username)!!.freeAmount-=(currentOrder.quantity*currentOrder.price)

            while (true) {

                if (currentOrder.quantity == 0)
                    break;

                var minSellerPrice = 1000000000;
                var orderID = -1;

                for (orderNumber in 0..n - 2) {
                    var orderPrev = orderList[orderNumber]

                    if ((orderPrev.status != "filled") && (currentOrder.type != orderPrev.type)) {
                        if (orderPrev.price < minSellerPrice) {
                            minSellerPrice = orderPrev.price
                            orderID = orderPrev.orderId
                        }
                    }
                }

                if (orderID != -1) {
                    var transQuantity = min(orderList[orderID].quantity, currentOrder.quantity)

                    orderList[orderID].quantity -= transQuantity
                    currentOrder.quantity -= transQuantity

                    walletList.get(username)!!.lockedAmount-=(transQuantity*minSellerPrice)
                    walletList.get(orderList.get(orderID).userName)!!.freeAmount+=(transQuantity*minSellerPrice)

                    inventorMap.get(orderList.get(orderID).userName)!!.lockESOP-=(transQuantity)
                    inventorMap.get(username)!!.freeESOP+=(transQuantity)



                    var tmpList: MutableList<Pair<Int, Int>> = mutableListOf()

                    if (!transactions.containsKey(currentOrder.orderId)) {
                        transactions.put(currentOrder.orderId, tmpList)
                    }

                    tmpList = transactions.get(currentOrder.orderId)!!

                    tmpList.add(Pair(transQuantity, minSellerPrice))

                    currentOrder.status = "partially filled"
                    orderList[orderID].status = "partially filled"

                    if (currentOrder.quantity == 0)
                        currentOrder.status = "filled"
                    if (orderList[orderID].quantity == 0)
                        orderList[orderID].status = "filled"

                } else
                    break;
            }

        } else {
            if (!OrderValidation().ifSufficientQuantity(username, currentOrder.quantity)) {
                val response = mutableMapOf<String, MutableList<String>>();
                var errorList = mutableListOf<String>("Insufficient quantity of ESOPs")
                response["error"] = errorList;

                return HttpResponse.badRequest(response);
            }

            inventorMap.get(username)!!.lockESOP+=(currentOrder.quantity)
            inventorMap.get(username)!!.freeESOP-=(currentOrder.quantity)

            while (true) {

                if (currentOrder.quantity == 0)
                    break;

                var minSellerPrice = -1;
                var orderID = -1;

                for (orderNumber in 0..n - 2) {


                    var orderPrev = orderList[orderNumber]

                    if ((orderPrev.status != "filled") && (currentOrder.type != orderPrev.type)) {

                        if (orderPrev.price > minSellerPrice) {
                            minSellerPrice = orderPrev.price
                            orderID = orderPrev.orderId
                        }
                    }
                }

                if (orderID != -1) {


                    var transQuantity = min(orderList[orderID].quantity, currentOrder.quantity)

                    orderList[orderID].quantity -= transQuantity
                    currentOrder.quantity -= transQuantity

                    walletList.get(username)!!.freeAmount+=(transQuantity*minSellerPrice)
                    walletList.get(orderList.get(orderID).userName)!!.lockedAmount-=(transQuantity*minSellerPrice)

                    inventorMap.get(orderList.get(orderID).userName)!!.freeESOP+=(transQuantity)
                    inventorMap.get(username)!!.lockESOP-=(transQuantity)



                    var tmpList: MutableList<Pair<Int, Int>> = mutableListOf()
                    if (!transactions.containsKey(currentOrder.orderId)) {
                        transactions.put(currentOrder.orderId, tmpList)
                    }

                    tmpList = transactions.get(currentOrder.orderId)!!

                    tmpList.add(Pair(transQuantity, minSellerPrice))


                    currentOrder.status = "partially filled"
                    orderList[orderID].status = "partially filled"


                    if (currentOrder.quantity == 0)
                        currentOrder.status = "filled"
                    if (orderList[orderID].quantity == 0)
                        orderList[orderID].status = "filled"

                } else
                    break;
            }


        }

        return HttpResponse.ok(currentOrder);
    }
}

