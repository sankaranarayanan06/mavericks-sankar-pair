
package com.example.services

import com.example.constants.orderList
import com.example.controller.walletList
import java.math.BigInteger

class WalletHandler{
    companion object {
        fun releaseExtraAmount(username: String,amount: BigInteger){
            walletList[username]!!.lockedAmount -= amount
            walletList[username]!!.freeAmount += amount
        }

        fun releaseLockAmount(username: String,amount: BigInteger) {
            walletList[username]!!.lockedAmount -= amount
        }

        fun addAmount(sellerID: Int,amount: BigInteger){

            walletList[orderList[sellerID]!!.userName]!!.freeAmount += amount
        }

        fun lockingAmountToPlaceOrder(username: String,amount: BigInteger){
            walletList[username]!!.lockedAmount += amount
            walletList[username]!!.freeAmount -= amount
        }

        fun getWalletInfo(username: String): MutableMap<String, BigInteger> {
            val walletInfo = mutableMapOf<String, BigInteger>()
            val userWallet = walletList[username]
            walletInfo["free"] = userWallet!!.freeAmount
            walletInfo["locked"] = userWallet!!.lockedAmount

            return walletInfo
        }
    }
}

