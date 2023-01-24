package com.example.validations;

import com.example.constants.maxQuantity

class WalletValidation {
    fun validations(walletAmount: Long, userAmount: Long): MutableList<String>{
        val walleterror = mutableListOf<String>()
        if (userAmount !in 1..maxQuantity) {
            walleterror.add("Amount out of Range. Max: 10 Million, Min: 1")
        }
        if(walletAmount+userAmount > maxQuantity){
            walleterror.add("Max wallet limit of 10 Million would be exceeded.")
        }
        return walleterror
    }
}
