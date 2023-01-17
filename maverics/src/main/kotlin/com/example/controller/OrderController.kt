package com.example.controller

import com.example.model.Order
import com.example.model.User
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import java.lang.Integer.min

var orderList= mutableListOf<Order>()
var orderID=0;

var transactions: MutableMap<Int,MutableList<Pair<Int,Int>>> =  mutableMapOf()


@Controller("/user")
class OrderController {

    @Post("/{username}/order")
    fun register(@Body body: JsonObject,@PathVariable username:String): Order {


        var currentOrder = Order()

        currentOrder.orderId= orderID
        orderID++
        currentOrder.quantity = body["quantity"].intValue
        currentOrder.type = body["type"].stringValue
        currentOrder.price = body["price"].intValue
        currentOrder.status = "unfilled"
        currentOrder.userName=username


        orderList.add(currentOrder)



        var n= orderList.size




        if(currentOrder.type=="BUY") {


            while(true) {

                if(currentOrder.quantity==0)
                    break;

                var minSellerPrice = -1;
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


                    var transQuantity=min(orderList[orderID].quantity,currentOrder.quantity)

                    orderList[orderID].quantity-=transQuantity
                    currentOrder.quantity-=transQuantity

                    var tmpList : MutableList<Pair<Int,Int>> = mutableListOf()
                    if(!transactions.containsKey(currentOrder.orderId))
                    {
                        transactions.put(currentOrder.orderId,tmpList)
                    }

                    tmpList= transactions.get(currentOrder.orderId)!!

                    tmpList.add(Pair(transQuantity,minSellerPrice))


                    currentOrder.status="partially filled"
                    orderList[orderID].status="partially filled"


                    if(currentOrder.quantity==0)
                        currentOrder.status="filled"
                    if(orderList[orderID].quantity==0)
                        orderList[orderID].status="filled"

                }
                else
                    break;
            }

        }
        else
        {

            while(true) {

                if(currentOrder.quantity==0)
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


                    var transQuantity=min(orderList[orderID].quantity,currentOrder.quantity)

                    orderList[orderID].quantity-=transQuantity
                    currentOrder.quantity-=transQuantity

                    var tmpList : MutableList<Pair<Int,Int>> = mutableListOf()
                    if(!transactions.containsKey(currentOrder.orderId))
                    {
                        transactions.put(currentOrder.orderId,tmpList)
                    }

                    tmpList= transactions.get(currentOrder.orderId)!!

                    tmpList.add(Pair(transQuantity,minSellerPrice))


                    currentOrder.status="partially filled"
                    orderList[orderID].status="partially filled"


                    if(currentOrder.quantity==0)
                        currentOrder.status="filled"
                    if(orderList[orderID].quantity==0)
                        orderList[orderID].status="filled"

                }
                else
                    break;
            }





        }







        return currentOrder
    }


}
