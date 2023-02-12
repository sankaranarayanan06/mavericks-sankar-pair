package com.example.services

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.vestingHistory
import com.example.constants.vestings
import com.example.controller.walletList
import com.example.model.inventory.EsopType
import com.example.model.inventory.Inventory
import com.example.model.user.User
import com.example.model.wallet.Wallet


fun addUser(newUser: User){
    val username = newUser.getUserName()
    allUsers[username] = newUser
    inventoryData[username] = mutableListOf(Inventory(type = EsopType.PERFORMANCE), Inventory(type = EsopType.NON_PERFORMANCE))
    walletList[username] = Wallet()
    vestings[username] = mutableListOf()
    vestingHistory[username] = mutableListOf()
}
