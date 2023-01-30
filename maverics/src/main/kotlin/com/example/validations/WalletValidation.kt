package com.example.validations;

import com.example.constants.Amounts


class WalletValidation {
    fun validations(walletAmount: Long, userAmount: Long): MutableList<String>{
        val walleterror = mutableListOf<String>()
        if (userAmount !in 1..Amounts.MAX_WALLET_AMOUNT) {
            walleterror.add("Amount out of Range. Max: ${Amounts.MAX_WALLET_AMOUNT}, Min: 1")
        }
        if(walletAmount+userAmount > Amounts.MAX_WALLET_AMOUNT){
            walleterror.add("Max wallet limit of ${Amounts.MAX_WALLET_AMOUNT} would be exceeded.")
        }
        return walleterror
    }
}
