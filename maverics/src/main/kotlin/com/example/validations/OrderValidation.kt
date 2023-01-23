package com.example.validations

import com.example.constants.inventorMap
import com.example.controller.walletList
import com.example.model.Inventory

class OrderValidation {

    fun ifSufficientAmountInWallet(username: String, amount: Long) : Boolean {
        if (walletList[username]!!.freeAmount < amount) {
            return false;
        }

        return true;
    }

    fun ifSufficientQuantity(username: String, quantity: Long,orderType: String) : Boolean {
        var inventoryList: MutableList<Inventory> = inventorMap[username]!!

        if(orderType == "PERFORMANCE"){
            return inventoryList[0].free >= quantity
        }
        else
        {
            return inventoryList[1].free >= quantity
        }
    }
}
