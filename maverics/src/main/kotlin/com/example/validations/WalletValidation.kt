package com.example.validations
import com.example.constants.Amounts

class WalletValidation {
    fun validations(walletAmount: Long, userAmount: Long): MutableList<String>{
        val walletError = mutableListOf<String>()
        if (userAmount !in 1..Amounts.MAX_WALLET_AMOUNT) {
            walletError.add("Amount out of Range Maximum: ${Amounts.MAX_WALLET_AMOUNT}, Minimum: 1")
        }
        if(walletAmount+userAmount > Amounts.MAX_WALLET_AMOUNT){
            walletError.add("Maximum wallet limit of amount ${Amounts.MAX_WALLET_AMOUNT} would be exceeded")
        }
        return walletError
    }
}
