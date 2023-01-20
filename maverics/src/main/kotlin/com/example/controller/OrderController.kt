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
var orderID = 0;

var transactions: MutableMap<Int, MutableList<Pair<Long, Long>>> = mutableMapOf()
/// quantity--price

fun performSells(currentOrder: Order, sellerUser: String) {

    var n = orderList.size
    while (true) {
        if (currentOrder.quantity == 0L) break;
        var maxBuyerPrice: Long = -1;
        var buyerOrderId = -1;
        for (orderNumber in 0..n - 1) {
            var orderPrev = orderList[orderNumber]

            if ((orderPrev.orderId!=currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price <= orderPrev.price)) {
                if (orderPrev.price > maxBuyerPrice) {
                    maxBuyerPrice = orderPrev.price
                    buyerOrderId = orderPrev.orderId
                }
            }
        }

        if (buyerOrderId != -1) {
            var transQuantity = min(orderList[buyerOrderId].quantity, currentOrder.quantity)
            orderList[buyerOrderId].quantity -= transQuantity
            currentOrder.quantity -= transQuantity

            // Return amount for high buy low sell scenario
            var returnAmount: Long = ((maxBuyerPrice - currentOrder.price) * transQuantity)
            walletList.get(orderList.get(buyerOrderId).userName)!!.lockedAmount -= returnAmount
            walletList.get(orderList.get(buyerOrderId).userName)!!.freeAmount += returnAmount

            // Get seller amount added to seller's account
            // Reduce the locked amount from buyer account
            // Add ESOPs to buyer account
            var orderTotal = transQuantity * currentOrder.price
            var platformCharge = (orderTotal * 2) / 100



            walletList.get(sellerUser)!!.freeAmount += (transQuantity * currentOrder.price - platformCharge)
            walletList.get(orderList.get(buyerOrderId).userName)!!.lockedAmount -= (transQuantity * currentOrder.price)


            inventorMap.get(orderList.get(buyerOrderId).userName)!![1].free += (transQuantity)
            if (currentOrder.esopType == "PERFORMANCE")
                inventorMap.get(sellerUser)!![0].locked -= (transQuantity)
            else
                inventorMap.get(sellerUser)!![1].locked -= (transQuantity)
            var newOrder: MutableList<Pair<Long, Long>> = mutableListOf()


            if (!transactions.containsKey(currentOrder.orderId)) {
                transactions.put(currentOrder.orderId, newOrder)
            }

            if (!transactions.containsKey(orderList.get(buyerOrderId).orderId)) {
                transactions.put(orderList.get(buyerOrderId).orderId, newOrder)
            }

            currentOrder.status = "partially filled"
            orderList[currentOrder.orderId].status = "partially filled"


            if (currentOrder.quantity == 0L) currentOrder.status = "filled"
            if (orderList[currentOrder.orderId].quantity == 0L) orderList[currentOrder.orderId].status = "filled"

        } else break;
    }

    // orderList.set(currentOrder.orderId,currentOrder)

}

@Controller("/user")
class OrderController {

    @Post("/{username}/order")
    fun register(@Body body: JsonObject, @PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            var currentOrder = Order()

            var transT: MutableList<Pair<Long, Long>> = mutableListOf()


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
                currentOrder.orderId = orderID++;
                orderList.add(currentOrder);
                n = orderList.size


                // Locking amount for order placing
                walletList.get(username)!!.lockedAmount += (currentOrder.quantity * currentOrder.price)
                walletList.get(username)!!.freeAmount -= (currentOrder.quantity * currentOrder.price)



                while (true) {
                    if (currentOrder.quantity.toLong() == 0L) break;

                    var minSellerPrice: Long = 1000000000000000;
                    var sellerID = -1;

                    for (orderNumber in 0..n - 1) {
                        var orderPrev = orderList[orderNumber]

                        // Order should match with SELL and should not be filled
                        if ((orderPrev.orderId != currentOrder.orderId) && (orderPrev.status != "filled") && (currentOrder.type != orderPrev.type) && (currentOrder.price >= orderPrev.price)) {
                            if (orderPrev.price < minSellerPrice) {
                                minSellerPrice = orderPrev.price.toLong()
                                sellerID = orderPrev.orderId
                            }
                        }
                    }
                    if (sellerID != -1) {
                        var transQuantity = min(orderList[sellerID].quantity, currentOrder.quantity)

                        orderList[sellerID].quantity -= transQuantity
                        currentOrder.quantity -= transQuantity

                        var orderTotal = minSellerPrice * transQuantity

                        var platformCharge = (orderTotal * 2) / 100

                        // Releasing extra amount from lock for partial matching scenario
                        walletList.get(username)!!.lockedAmount -= ((currentOrder.price - minSellerPrice) * transQuantity)
                        walletList.get(username)!!.freeAmount += ((currentOrder.price - minSellerPrice) * transQuantity)

                        // Releasing lock amount worth actual transaction
                        walletList.get(username)!!.lockedAmount -= (transQuantity * minSellerPrice)
                        walletList.get(orderList.get(sellerID).userName)!!.freeAmount += (transQuantity * minSellerPrice - platformCharge)

                        // Reducing the esops from seller account
                        if (orderList.get(sellerID).esopType == "PERFORMANCE") {
                            inventorMap.get(orderList.get(sellerID).userName)!![0].locked -= (transQuantity)
                        } else {
                            inventorMap.get(orderList.get(sellerID).userName)!![1].locked -= (transQuantity)
                        }

                        //Adding ESOP to buyers account
                        inventorMap.get(username)!![1].free += (transQuantity)

                        // Updating buyers transactions
                        if(!transactions.containsKey(currentOrder.orderId)){
                            transactions.put(currentOrder.orderId, mutableListOf<Pair<Long,Long>>())
                        }

                        transactions.get(currentOrder.orderId)!!.add(Pair(transQuantity, minSellerPrice))

                        // Updating seller entries
                        if(!transactions.containsKey(sellerID)){
                            transactions.put(sellerID, mutableListOf<Pair<Long,Long>>())
                        }
                        transactions.get(sellerID)!!.add(Pair(transQuantity,minSellerPrice))

                        currentOrder.status = "partially filled"
                        orderList[sellerID].status = "partially filled"

                        if (currentOrder.quantity == 0L) currentOrder.status = "filled"
                        if (orderList[sellerID].quantity == 0L) orderList[sellerID].status = "filled"


                    }
                    else
                    {
                        break;
                    }
                }

//                orderList.set(currentOrder.orderId, currentOrder)

            } else {

                var quantity = body["quantity"].longValue

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

                    println("first")
                    inventoryList[0].free -= body["quantity"].longValue
                    inventoryList[0].locked += body["quantity"].longValue

                    orderOne.orderId = orderID++
                    orderOne.quantity = quantity
                    orderOne.price = body["price"].longValue
                    orderOne.type = body["type"].stringValue
                    orderOne.status = "unfilled"
                    orderOne.userName = username
                    orderOne.esopType = "PERFORMANCE"

                    orderList.add(orderOne);
                    transactions.put(orderID - 1, transT);

                    performSells(orderOne, username)

                    //
                } else {
                    var quantityFirst = inventoryList[0].free
                    var quantitySecond = currentOrder.quantity - quantityFirst


                    inventoryList[0].free -= quantityFirst
                    inventoryList[0].locked += quantityFirst


                    inventoryList[1].free -= quantitySecond
                    inventoryList[1].locked += quantitySecond

                    orderOne.orderId = orderID++
                    orderOne.quantity = quantityFirst
                    orderOne.price = body["price"].longValue
                    orderOne.type = body["type"].stringValue
                    orderOne.status = "unfilled"
                    orderOne.userName = username
                    orderOne.esopType = "PERFORMANCE"

                    orderList.add(orderOne);
                    transactions.put(orderID - 1, mutableListOf<Pair<Long,Long>>());
                    performSells(orderOne, username)


                    orderTwo.orderId = orderID++
                    orderTwo.quantity = quantitySecond
                    orderTwo.price = body["price"].longValue
                    orderTwo.type = body["type"].stringValue
                    orderTwo.status = "unfilled"
                    orderTwo.userName = username
                    orderTwo.esopType = "NON_PERFORMANCE"
                    orderList.add(orderTwo);
                    println("Added transactions to order " + orderID)
                    transactions.put(orderID - 1, mutableListOf<Pair<Long,Long>>());
                    performSells(orderTwo, username)

                }

            }

            var ret = HashMap<String,Any>();

            ret["userName"] = currentOrder.userName
            ret["quantity"] = currentOrder.quantity
            ret["price"] = currentOrder.price
            ret["type"] = currentOrder.type

            return HttpResponse.ok(ret);
        } else {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;
            return HttpResponse.badRequest(response);
        }

    }
}
