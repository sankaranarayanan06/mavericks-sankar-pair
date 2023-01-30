package com.example.services

import com.example.constants.allUsers
import com.example.constants.inventoryData
import com.example.constants.vestingHistory
import com.example.constants.vestings
import com.example.controller.walletList
import com.example.model.Inventory
import com.example.model.User
import com.example.model.Wallet


fun addUser(newUser: User){
    val username = newUser.userName
    allUsers[username] = newUser
    inventoryData[username] = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
    walletList[username] = Wallet()
    vestings.put(username, mutableListOf())
    vestingHistory.put(username, mutableListOf())
}
