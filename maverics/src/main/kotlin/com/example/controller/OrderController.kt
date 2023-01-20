package com.example.controller

import com.example.model.Inventory
import com.example.model.Order
import com.example.validations.OrderValidation
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import java.lang.Integer.min
import kotlin.math.min

var orderList = mutableListOf<Order>()
var orderID = -1;

var transactions: MutableMap<Int, MutableList<Pair<Long, Long>>> = mutableMapOf()
/// quantity--price

@Controller("/user")
class OrderController {

    @Post("/{username}/order")
    fun register(@Body body: JsonObject, @PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            orderID++;
            var currentOrder = Order()

            var transT: MutableList<Pair<Long, Long>> = mutableListOf()


            currentOrder.orderId = orderID;
            currentOrder.quantity = body["quantity"].longValue;
            currentOrder.type = body["type"].stringValue;
            currentOrder.price = body["price"].longValue;
            currentOrder.status = "unfilled";
            currentOrder.userName = username;


            var n = orderList.size

            if (currentOrder.type == "BUY") {
                // Buyer section
                var orderAmount = currentOrder.price * currentOrder.quantity;

                // validations
                if (!OrderValidation().ifSufficientAmountInWallet(username, orderAmount)) {
                    val response = mutableMapOf<String, MutableList<String>>();
                    var errorList = mutableListOf<String>("Insufficient amount in wallet")
                    response["error"] = errorList;

                    return HttpResponse.badRequest(response);
                }

                orderList.add(currentOrder);
                transactions.put(orderID, transT)
                n = orderList.size


                // Locking amount for order placing
                walletList.get(username)!!.lockedAmount += (currentOrder.quantity * currentOrder.price)
                walletList.get(username)!!.freeAmount -= (currentOrder.quantity * currentOrder.price)



                while (true) {

                    if (currentOrder.quantity.toInt() == 0) break;

                    var minSellerPrice:Long = 1000000000;
                    var orderID = -1;

                    for (orderNumber in 0..n - 2) {
                        var orderPrev = orderList[orderNumber]

                        // Order should match with SELL and should not be filled
                        if ((orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                            if (orderPrev.price < minSellerPrice) {
                                minSellerPrice = orderPrev.price.toLong()
                                orderID = orderPrev.orderId
                            }
                        }
                    }

                    if (orderID != -1) {
                        var transQuantity = min(orderList[orderID].quantity, currentOrder.quantity)

                        orderList[orderID].quantity -= transQuantity
                        currentOrder.quantity -= transQuantity

                        // Releasing extra amount from lock for partial matching scenario
                        walletList.get(username)!!.lockedAmount -= ((currentOrder.price - minSellerPrice) * transQuantity)
                        walletList.get(username)!!.freeAmount += ((currentOrder.price - minSellerPrice) * transQuantity)

                        // Releasing lock amount worth actual transaction
                        walletList.get(username)!!.lockedAmount -= (transQuantity * minSellerPrice)
                        walletList.get(orderList.get(orderID).userName)!!.freeAmount += (transQuantity * minSellerPrice)

                        var inventoryList: MutableList<Inventory> = inventorMap[orderList.get(orderID).userName]!!
                        if (inventoryList[0].type == "PERFORMANCE") {

                        }
//                        inventorMap.get(orderList.get(orderID).userName)!!.lockESOP -= (transQuantity)
//                        inventorMap.get(username)!!.freeESOP += (transQuantity)


                        var newOrder: MutableList<Pair<Long, Long>> = mutableListOf()

                        if (!transactions.containsKey(currentOrder.orderId)) {
                            transactions.put(currentOrder.orderId, newOrder)
                        }
                        if (!transactions.containsKey(orderID)) {
                            transactions.put(orderID, newOrder)
                        }


                        newOrder = transactions.get(currentOrder.orderId)!!
                        newOrder.add(Pair(transQuantity, minSellerPrice))

                        newOrder = transactions.get(orderID)!!
                        newOrder.add(Pair(transQuantity, minSellerPrice))





                        currentOrder.status = "partially filled"
                        orderList[orderID].status = "partially filled"

                        if (currentOrder.quantity == 0L) currentOrder.status = "filled"
                        if (orderList[orderID].quantity == 0L) orderList[orderID].status = "filled"

                    } else break;
                }

            } else {
                var quantity = body["quantity"].longValue

                while (quantity > 0) {
                    if (!OrderValidation().ifSufficientQuantity(username, currentOrder.quantity)) {
                        val response = mutableMapOf<String, MutableList<String>>();
                        var errorList = mutableListOf<String>("Insufficient quantity of ESOPs")
                        response["error"] = errorList;

                        return HttpResponse.badRequest(response);
                    }

                    var inventoryList: MutableList<Inventory> = inventorMap[username]!!

                    var orderOne: Order = Order()
                    var orderTwo: Order = Order()


                    if (inventoryList[0].free - body["quantity"].longValue >= 0) {
                        inventoryList[0].free -= body["quantity"].longValue
                        inventoryList[0].locked += body["quantity"].longValue

                        orderOne.orderId = orderID
                        orderOne.quantity = quantity
                        orderOne.price = body["price"].longValue
                        orderOne.type = body["type"].stringValue
                        orderOne.status = "unfilled"
                        orderOne.userName = username

                        orderList.add(orderOne);
                        transactions.put(orderID, transT);

                        //
                    } else {
                        var num = currentOrder.quantity - inventoryList[0].free
                        inventoryList[0].free = 0
                        inventoryList[0].locked += inventoryList[0].free
                        inventoryList[1].free -= num
                        inventoryList[1].locked += num

                        orderOne.orderId = orderID
                        orderOne.quantity = quantity
                        orderOne.price = body["price"].longValue
                        orderOne.type = body["type"].stringValue
                        orderOne.status = "unfilled"
                        orderOne.userName = username

                        orderTwo.orderId = orderID
                        orderTwo.quantity = quantity
                        orderTwo.price = body["price"].longValue
                        orderTwo.type = body["type"].stringValue
                        orderTwo.status = "unfilled"
                        orderTwo.userName = username
                    }


                    n = orderList.size

//
//
//                    inventorMap.get(username)!!.lockESOP += (currentOrder.quantity)
//                    inventorMap.get(username)!!.freeESOP -= (currentOrder.quantity)

                    while (true) {

                        if (currentOrder.quantity == 0L) break;

                        var maxBuyerPrice: Long = -1;
                        var orderID = -1;

                        for (orderNumber in 0..n - 2) {


                            var orderPrev = orderList[orderNumber]

                            if ((orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price <= orderPrev.price)) {

                                if (orderPrev.price > maxBuyerPrice) {
                                    maxBuyerPrice = orderPrev.price
                                    orderID = orderPrev.orderId
                                }
                            }
                        }
                        if (orderID != -1) {


                            var transQuantity = min(orderList[orderID].quantity, currentOrder.quantity)

                            orderList[orderID].quantity -= transQuantity
                            currentOrder.quantity -= transQuantity

                            var orderTotal: Long = ((maxBuyerPrice - currentOrder.price) * transQuantity)
                            walletList.get(orderList.get(orderID).userName)!!.lockedAmount -= orderTotal
                            walletList.get(orderList.get(orderID).userName)!!.freeAmount += orderTotal


                            walletList.get(username)!!.freeAmount += (transQuantity * maxBuyerPrice)
                            walletList.get(orderList.get(orderID).userName)!!.lockedAmount -= (transQuantity * maxBuyerPrice)

//                            inventorMap.get(orderList.get(orderID).userName)!!.freeESOP += (transQuantity)
//                            inventorMap.get(username)!!.lockESOP -= (transQuantity)


                            var newOrder: MutableList<Pair<Long, Long>> = mutableListOf()





                            if (!transactions.containsKey(currentOrder.orderId)) {
                                transactions.put(currentOrder.orderId, newOrder)
                            }
                            if (!transactions.containsKey(orderList.get(orderID).orderId)) {
                                transactions.put(orderList.get(orderID).orderId, newOrder)
                            }


                            newOrder = transactions.get(currentOrder.orderId)!!
                            newOrder.add(Pair(transQuantity, maxBuyerPrice))

                            newOrder = transactions.get(orderList.get(orderID).orderId)!!
                            newOrder.add(Pair(transQuantity, maxBuyerPrice))







                            currentOrder.status = "partially filled"
                            orderList[orderID].status = "partially filled"


                            if (currentOrder.quantity == 0L) currentOrder.status = "filled"
                            if (orderList[orderID].quantity == 0L) orderList[orderID].status = "filled"

                        } else break;
                    }
                }


            }


            var ret: Order = Order()
            ret.orderId = currentOrder.orderId + 1
            ret.userName = currentOrder.userName
            ret.quantity = currentOrder.quantity
            ret.status = currentOrder.status
            ret.price = currentOrder.price

            return HttpResponse.ok(ret);
        } else {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;
            return HttpResponse.badRequest(response);
        }

    }
}
