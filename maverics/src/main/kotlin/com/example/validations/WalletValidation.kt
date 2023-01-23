package com.example.validations;

import com.example.controller.maxWalletAmount


class WalletValidation {
    fun validations(walletAmount: Long, userAmount: Long): MutableList<String>{
        val walleterror = mutableListOf<String>()
        if (userAmount !in 1..maxWalletAmount) {
            walleterror.add("Amount out of Range. Max: 100 Crore, Min: 1")
        }
        if(walletAmount+userAmount > maxWalletAmount){
            walleterror.add("Max wallet limit of 100 Crores would be exceeded.")
        }
        return walleterror
    }
}
