package com.example.validations

import com.example.controller.inventorMap
import com.example.controller.walletList

class OrderValidation {

    fun ifSufficientAmountInWallet(username: String, amount: Long) : Boolean {
        if (walletList[username]!!.freeAmount < amount) {
            return false;
        }

        return true;
    }

    fun ifSufficientQuantity(username: String, quantity: Long) : Boolean {
        if (inventorMap[username]!!.freeESOP < quantity) {
            return false;
        }

        return true;
    }
}