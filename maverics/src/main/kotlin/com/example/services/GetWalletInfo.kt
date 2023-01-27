package com.example.services

import com.example.controller.walletList


fun getWalletInfo(username: String): MutableMap<String, Long> {
    val walletInfo = mutableMapOf<String, Long>()
    val userWallet = walletList[username]
    walletInfo["free"] = userWallet!!.freeAmount
    walletInfo["locked"] = userWallet.lockedAmount

    return walletInfo
}
