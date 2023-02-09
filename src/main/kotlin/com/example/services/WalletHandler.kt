package com.example.services

import com.example.constants.orderList
import com.example.controller.walletList
import java.math.BigInteger

class WalletHandler {
    companion object {

        fun addFreeAmountInWallet(username: String, amount: BigInteger) {
            walletList[username]!!.freeAmount += amount

        }

        fun discardedFreeAmountFromWallet(username: String, amount: BigInteger) {
            walletList[username]!!.freeAmount -= amount
        }

        fun addLockedAmountInWallet(username: String, amount: BigInteger) {
            walletList[username]!!.lockedAmount += amount
        }

        fun discardLockedAmountFromWallet(username: String, amount: BigInteger) {
            walletList[username]!!.lockedAmount -= amount
        }

        fun getFreeAmount(username: String): BigInteger {
            return walletList[username]!!.freeAmount

        }

        fun getLockedAmount(username: String): BigInteger {
            return walletList[username]!!.lockedAmount
        }

        fun addAmount(sellerID: Int, amount: BigInteger) {

            walletList[orderList[sellerID]!!.userName]!!.freeAmount += amount
        }


        fun getWalletInfo(username: String): MutableMap<String, BigInteger> {
            val walletInfo = mutableMapOf<String, BigInteger>()
            val userWallet = walletList[username]
            walletInfo["free"] = userWallet!!.freeAmount
            walletInfo["locked"] = userWallet.lockedAmount

            return walletInfo
        }
    }
}

