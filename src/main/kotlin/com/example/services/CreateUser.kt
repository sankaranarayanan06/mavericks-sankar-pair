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

fun createUser(firstName: String, lastName: String, phoneNumber: String, email: String, userName: String) {
    val newUser = User(firstName, lastName, phoneNumber, email, userName)
    allUsers[userName] = newUser
    inventoryData[userName] = mutableListOf(Inventory(type = EsopType.PERFORMANCE), Inventory(type = EsopType.NON_PERFORMANCE))
    walletList[userName] = Wallet()
    vestings[userName] = mutableListOf()
    vestingHistory[userName] = mutableListOf()
}