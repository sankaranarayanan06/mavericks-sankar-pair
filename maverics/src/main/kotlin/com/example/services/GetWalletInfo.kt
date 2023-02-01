package com.example.services

import com.example.controller.walletList
import java.math.BigInteger


fun getWalletInfo(username: String): MutableMap<String, BigInteger> {
    val walletInfo = mutableMapOf<String, BigInteger>()
    val userWallet = walletList[username]
    walletInfo["free"] = userWallet!!.freeAmount
    walletInfo["locked"] = userWallet.lockedAmount

    return walletInfo
}
