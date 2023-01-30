package com.example.model

class Order(
    var price: Long = 0,
    var currentQuantity: Long = 0,
    var placedQuantity: Long = 0,
    var status: String = "",
    var type: String = "",
    var esopType: String = "NON_PERFORMANCE",
    var userName: String = "",
) {
    val orderId: Int = orderIdCounter++

    companion object {
        var orderIdCounter = 0
    }
}
