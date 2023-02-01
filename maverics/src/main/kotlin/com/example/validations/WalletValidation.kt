package com.example.validations

import com.example.constants.Limits
import java.math.BigInteger

class WalletValidation {
    fun validations(walletAmount: Long, userAmount: Long): MutableList<String> {
        val walletError = mutableListOf<String>()
        if (BigInteger.valueOf(userAmount) !in BigInteger.ONE..Limits.MAX_WALLET_AMOUNT) {
            walletError.add("Amount out of Range Maximum: ${Limits.MAX_WALLET_AMOUNT}, Minimum: 1")
        }
        if (BigInteger.valueOf(walletAmount + userAmount) > Limits.MAX_WALLET_AMOUNT) {
            walletError.add("Maximum wallet limit of amount ${Limits.MAX_WALLET_AMOUNT} would be exceeded")
        }
        return walletError
    }
}
