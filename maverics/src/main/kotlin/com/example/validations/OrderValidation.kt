package com.example.validations

import com.example.controller.inventorMap
import com.example.controller.walletList
import com.example.model.Inventory

class OrderValidation {

    fun ifSufficientAmountInWallet(username: String, amount: Long) : Boolean {
        if (walletList[username]!!.freeAmount < amount) {
            return false;
        }

        return true;
    }

    fun ifSufficientQuantity(username: String, quantity: Long) : Boolean {
        var inventoryList: MutableList<Inventory> = inventorMap[username]!!

        if(inventoryList[0].free + inventoryList[1].free< quantity) {
            return false
        }

        return true;
    }
}